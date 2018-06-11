package org.xellossryan.output;

import android.os.Environment;

import org.xellossryan.abstractlayer.EncoderLayer;
import org.xellossryan.lame.ParameterBuilder;
import org.xellossryan.log.L;
import org.xellossryan.recorder.EncodeArguments;

import java.io.File;
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
    BlockingQueue<BufferedFrame> taskQueue = new ArrayBlockingQueue<>(BUFFER_QUEUE_CAPACITY);

    /**
     * 编码过程标识，为false时编码队列线程结束
     */
    final AtomicBoolean isEncodingState = new AtomicBoolean(false);
    private int bufferSize;
    private EncoderLayer encoder;

    private int sampleRateInHz = 44100;
    private int channelConfig = 2;//2 for stereo, 1 for mono
    private String storePath;

    /**
     * 传入编码器实例
     *
     * @param encoder
     */
    public FrameEncodeQueue(EncoderLayer encoder) {
        this.encoder = encoder;
        setName("FrameEncodeQueue");
    }

    /**
     * 允许写入编码后的字节流到文件
     * <p>
     * 默认为true
     */
    public boolean allowWriteToFile = true;

    public FrameEncodeQueue setAllowWritingToFile(boolean allowWriteToFile) {
        this.allowWriteToFile = allowWriteToFile;
        return this;
    }

    public boolean isAllowWriteToFile() {
        return allowWriteToFile;
    }

    public void preparePool(int bufferSize) {
        this.bufferSize = bufferSize;
        sampleRateInHz = EncodeArguments.DEFAULT_SAMPLING_RATE;
        channelConfig = EncodeArguments.DEFAULT_ENCODER_IN_CHANNEL;
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
        L.v("Running: " + getName());
        super.run();
        File mp3outputFile = new File(storePath);
        if (allowWriteToFile && !mp3outputFile.getParentFile().exists()) {
            boolean mkdirs = mp3outputFile.getParentFile().mkdirs();
            if (!mkdirs) {
                return;
            }
        }
        FileOutputStream encodedOutputStream = null;
        BufferedFrame frame = null;
        try {
            if (allowWriteToFile) {
                encodedOutputStream = new FileOutputStream(mp3outputFile);
            }
            L.w(getName() + ": Task queue START: " + taskQueue.remainingCapacity() + "  " +
                    "TaskQueue#isEmpty()?" + taskQueue.isEmpty());
            // L.w(getName() + ": isEncodingState: " + isEncodingState);
            while (!taskQueue.isEmpty() || isEncodingState.get()) {
                // L.v(getName() + ": Task queue take: " + taskQueue.remainingCapacity());
                frame = taskQueue.take();
                if (frame.pcmBuffer != null) {
                    //Encoding
                    int encodeLength = 0;
                    if (channelConfig == 1) {
                        encodeLength = encoder.encode(frame.pcmBuffer, frame.pcmBuffer, frame.encodedBuffer, frame.pcmBufferReadSize);
                    } else if (channelConfig == 2) {
                        encodeLength = encoder.encodeInterleaved(frame.pcmBuffer, frame.encodedBuffer, frame.pcmBufferReadSize >> 1);
                    }
                    if (encodeLength > 0) {
                        L.i(getName() + ": Encoding Size: " + encodeLength);
                        //WriteInFile
                        if (encodedOutputStream != null) {
                            encodedOutputStream.write(frame.encodedBuffer, 0, encodeLength);
                        }
                    }
                    //release
                    returnBack(frame);
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            L.v(getName() + ": End flushing...");
            try {
                if (encodedOutputStream != null) {
                    returnBack(frame);
                    if (frame != null) {
                        encoder.flush(frame.encodedBuffer);
                        encodedOutputStream.write(frame.encodedBuffer);
                    }
                    encodedOutputStream.close();
                    if (onEncodingEnd != null) {
                        onEncodingEnd.onEnd(mp3outputFile);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            L.v(getName() + ": Encoder flushing OK");
        }
        L.w(getName() + ": STOPPED!");
    }

    public OnEncodingEnd onEncodingEnd;

    public FrameEncodeQueue setOnEncodingEnd(OnEncodingEnd onEncodingEnd) {
        this.onEncodingEnd = onEncodingEnd;
        return this;
    }

    public void setStorePath(String storePath) {
        this.storePath = storePath;
    }

    public interface OnEncodingEnd {
        public void onEnd(File audioFile);
    }

    /**
     * 添加到队列
     *
     * @param buffer
     * @param encodedBuffer
     * @param bufferSizeInShorts
     */
    public void addInQueue(short[] buffer, byte[] encodedBuffer, int bufferSizeInShorts) {
        BufferedFrame filledBuffer = borrow().fillBuffer(buffer, encodedBuffer, bufferSizeInShorts);
        try {
            //L.v(getName() + ": Add into queue, remain capacity: " + taskQueue.remainingCapacity());
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

    public void flush(byte[] lastOneBuffer) {
        BufferedFrame lastOneBufferFrame = borrow()
                .resetAsIdle(bufferSize)
                .setPcmBuffer(null)
                .setEncodedBuffer(lastOneBuffer);
        taskQueue.add(lastOneBufferFrame);
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
         * PCM读取大小
         */
        public int pcmBufferReadSize;

        /**
         * 重置为空包，节省空间
         *
         * @return
         */
        public BufferedFrame resetAsIdle(int bufferSizeInBytes) {
            this.pcmBufferReadSize = bufferSizeInBytes;
            pcmBuffer = new short[bufferSizeInBytes];
            encodedBuffer = new byte[bufferSizeInBytes];
            isIdle = true;
            return this;
        }

        public BufferedFrame fillBuffer(short[] data, byte[] encodedBuffer, int bufferSize) {
            this.pcmBufferReadSize = bufferSize;
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

        public BufferedFrame setPcmBufferReadSize(int pcmBufferReadSize) {
            this.pcmBufferReadSize = pcmBufferReadSize;
            return this;
        }

        public boolean isIdle() {
            return isIdle;
        }

        public short[] getPcmBuffer() {
            return pcmBuffer;
        }

        public int getPcmBufferReadSize() {
            return pcmBufferReadSize;
        }

    }
}
