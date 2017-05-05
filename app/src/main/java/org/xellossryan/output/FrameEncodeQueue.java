package org.xellossryan.output;

import android.os.Environment;

import org.xellossryan.abstractlayer.EncoderLayer;
import org.xellossryan.lame.ParameterBuilder;
import org.xellossryan.log.L;
import org.xellossryan.recorder.EncodeArguments;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Liang on 2017/5/5.
 */
public class FrameEncodeQueue extends Thread {
    public static final int BUFFER_QUEUE_CAPACITY = 100;

    BlockingQueue<BufferedFrame> idleBufferPool = new ArrayBlockingQueue<>(BUFFER_QUEUE_CAPACITY);
    BlockingQueue<BufferedFrame> taskQueue = new ArrayBlockingQueue<>(100);

    /**
     * 编码过程标识，为false时编码队列线程结束
     */
    final AtomicBoolean isEncodingState = new AtomicBoolean(false);
    private int bufferSize;
    private EncoderLayer encoder;

    private int sampleRateInHz = 44100;
    private int channelConfig = 12;//12 for stereo, 10 for mono

    /**
     * 传入编码器实例
     *
     * @param encoder
     */
    public FrameEncodeQueue(EncoderLayer encoder) {
        this.encoder = encoder;
    }

    public void preparePool(int bufferSize) {
        this.bufferSize = bufferSize;
        sampleRateInHz = EncodeArguments.DEFAULT_SAMPLING_RATE;
        channelConfig = EncodeArguments.DEFAULT_CHANNEL_CONFIG;
        L.d("initial encoder.");
        encoder.initEncoder();
        encoder.initParameters(ParameterBuilder.builder()
                .setInSampleRate(sampleRateInHz)
                .setInChannels(channelConfig)
                .setOutBitrate(EncodeArguments.DEFAULT_ENCODER_BIT_RATE)
                .setQuality(EncodeArguments.DEFAULT_ENCODER_QUALITY)
        );
        L.d("initial Idle queue.");
        for (int i = 0; i < BUFFER_QUEUE_CAPACITY; i++) {
            idleBufferPool.offer(new BufferedFrame().resetAsIdle(bufferSize));
        }
        taskQueue.clear();
    }

    @Override
    public void run() {
        super.run();
        try {

            File mp3outputFile = new File(Environment.getExternalStorageDirectory() + "/zVoice/Aka_" + System.currentTimeMillis() + ".mp3");
            if (!mp3outputFile.getParentFile().exists()) {
                boolean mkdirs = mp3outputFile.getParentFile().mkdirs();
                if (!mkdirs) {
                    return;
                }
            }
            FileOutputStream outputStream = new FileOutputStream(mp3outputFile);

            BufferedFrame frame = null;

            L.w("Task queue START: " + taskQueue.remainingCapacity());
            while (!taskQueue.isEmpty() && isEncodingState.get()) {
                L.v("Task queue take: " + taskQueue.remainingCapacity());
                frame = taskQueue.take();
                //Encoding
                encoder.encodeInterleaved(frame.pcmBuffer, frame.encodedBuffer, frame.bufferSizeInBytes >> 1);
                L.i("Encoding... " + frame.bufferSizeInBytes);
                //WriteInFile
                outputStream.write(frame.encodedBuffer);
                //release
                //returnBack(frame);
            }
            if (frame == null) {
                L.e("Unexpected Ending....");
                frame = taskQueue.take();
            }
            L.v("End flushing...");
            encoder.flush(frame.encodedBuffer);
            outputStream.write(frame.encodedBuffer);
            outputStream.close();
            L.v("OK");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    /**
     * 添加到队列
     *
     * @param buffer
     * @param encodedBuffer
     * @param bufferSizeInBytes
     */
    public void addInQueue(short[] buffer, byte[] encodedBuffer, int bufferSizeInBytes) {
        BufferedFrame filledBuffer = borrow().fillBuffer(buffer, encodedBuffer, bufferSizeInBytes);
        try {
            L.v("add into queue, remain capacity: " + taskQueue.remainingCapacity());
            taskQueue.put(filledBuffer);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 从空闲缓冲对象中借走，如果获取队列为空了，或者获取过程发生意外情况，就返回一个新的缓冲对象
     *
     * @return
     */
    public BufferedFrame borrow() {
        try {
            if (!idleBufferPool.isEmpty()) {
                return idleBufferPool.take();
            } else {
                return new BufferedFrame().resetAsIdle(bufferSize);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new BufferedFrame().resetAsIdle(bufferSize);
    }

    /**
     * 归还到空闲对象队列中，如果空闲对象队列已经没有剩余空间了，那就放弃归还，直接扔掉
     *
     * @param bufferedFrame
     */
    public void returnBack(BufferedFrame bufferedFrame) {
        if (taskQueue.contains(bufferedFrame)) {
            taskQueue.remove(bufferedFrame);
        }
        if (idleBufferPool.remainingCapacity() > 0) {
            idleBufferPool.offer(bufferedFrame);
        }
    }

    @Override
    public synchronized void start() {
        isEncodingState.set(true);
        super.start();
    }

    public void setStopEncoding() {
        isEncodingState.set(false);
    }

    public String version() {
        return encoder.version();
    }

    public int close() {
        return encoder.close();
    }

    /**
     * 缓冲区包
     * <p>
     * 假设每个帧是3352个short采样，带上int 共3552x2+4字节，最大长度为100的队列则共(3552x2+4)x100字节，队列总长度占用空间约0.67M
     */
    public static class BufferedFrame {
        /**
         * 是否为空闲缓冲区包
         */
        boolean isIdle;
        /**
         * PCM交织数据缓冲区
         */
        public short[] pcmBuffer;

        /**
         * 已编码缓冲区
         */
        public byte[] encodedBuffer;
        /**
         * 已编码缓冲区大小
         */
        public int bufferSizeInBytes;

        /**
         * 重置为空包，节省空间
         *
         * @return
         */
        public BufferedFrame resetAsIdle(int bufferSizeInBytes) {
            this.bufferSizeInBytes = bufferSizeInBytes;
            pcmBuffer = new short[bufferSizeInBytes / 2];
            encodedBuffer = new byte[bufferSizeInBytes];
            isIdle = true;
            return this;
        }

        public BufferedFrame fillBuffer(short[] data, byte[] encodedBuffer, int bufferSize) {
            this.bufferSizeInBytes = bufferSize;
            this.pcmBuffer = data;
            this.encodedBuffer = encodedBuffer;
            this.isIdle = false;
            return this;
        }

        public byte[] getEncodedBuffer() {
            return encodedBuffer;
        }

        public BufferedFrame setEncodedBuffer(byte[] encodedBuffer) {
            this.encodedBuffer = encodedBuffer;
            return this;
        }

        public BufferedFrame setPcmBuffer(short[] pcmBuffer) {
            this.pcmBuffer = pcmBuffer;
            return this;
        }

        public BufferedFrame setBufferSizeInBytes(int bufferSizeInBytes) {
            this.bufferSizeInBytes = bufferSizeInBytes;
            return this;
        }

        public boolean isIdle() {
            return isIdle;
        }

        public short[] getPcmBuffer() {
            return pcmBuffer;
        }

        public int getBufferSizeInBytes() {
            return bufferSizeInBytes;
        }

    }
}