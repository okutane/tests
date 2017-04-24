package ru.urururu.tests.chatfuel;

import com.google.common.primitives.Chars;
import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.api.StatefulRedisConnection;
import com.lambdaworks.redis.api.sync.RedisCommands;
import com.lambdaworks.redis.api.sync.RedisStringCommands;
import com.lambdaworks.redis.codec.RedisCodec;
import io.netty.buffer.Unpooled;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 * @author <a href="mailto:dmitriy.g.matveev@gmail.com">Dmitry Matveev</a>
 */
public class RedisRpsEnforcer implements RpsEnforcer {
    private final RedisCommands<Integer, Integer> sync;

    RedisRpsEnforcer() {
        RedisClient client = RedisClient.create("redis://localhost");
        StatefulRedisConnection<Integer, Integer> connection = client.connect(new RedisCodec<Integer, Integer>() {
            ThreadLocal<ByteBuffer> keyBuffer = ThreadLocal.withInitial(() -> ByteBuffer.allocate(4));
            ThreadLocal<ByteBuffer> valueBuffer = ThreadLocal.withInitial(() -> ByteBuffer.allocate(4));

            @Override
            public Integer decodeKey(ByteBuffer byteBuffer) {
                return byteBuffer.getInt();
            }

            @Override
            public Integer decodeValue(ByteBuffer byteBuffer) {
                return byteBuffer.getInt();
            }

            @Override
            public ByteBuffer encodeKey(Integer integer) {
                ByteBuffer bb = keyBuffer.get();
                bb.reset();
                bb.putInt(integer);
                return bb;
            }

            @Override
            public ByteBuffer encodeValue(Integer integer) {
                ByteBuffer bb = valueBuffer.get();
                bb.reset();
                bb.putInt(integer);
                return bb;
            }
        });
        sync = connection.sync();
        //String value = sync.get("key");
    }

    @Override
    public boolean allowed() {
        sync.get(1);
        return true;
    }
}
