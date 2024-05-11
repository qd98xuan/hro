package com.linzen.database.config;

import com.github.yitter.contract.IdGeneratorOptions;
import com.github.yitter.idgen.YitIdHelper;
import com.linzen.util.CacheKeyUtil;
import com.linzen.util.context.SpringContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class IdGeneratorConfig {

    private static final String ID_IDX = CacheKeyUtil.IDGENERATOR + "Index:";
    private RedisTemplate<String, Long> redisTemplate;
    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    /**
     * ID缓存有效时间 定时刷新有效期
     */
    private static final long CACHE_TIMEOUT = 60L * 60 * 24;
    //30分钟续期一次 如果Redis被清空可以早点续期
    private static final long ScheduleTimeout = 60L * 30;
    private static final byte WorkerIdBitLength = 16;
    //65535 参数为shot 最大值为Short.MAX_VALUE
    private static final int MaxWorkerIdNumberByMode = (1 << WorkerIdBitLength) - 1 > Short.MAX_VALUE ? Short.MAX_VALUE : (1 << WorkerIdBitLength) - 1;
    private static ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;

    private short workerId = -1;
    private String cacheKey;


    /**
     * 初始化雪花生成器WorkerID， 通过Redis实现集群获取不同的编号， 如果相同会出现ID重复
     */
    @Bean
    private void initIdWorker() {
        redisTemplate = SpringContext.getBean("redisTemplate");
        if (redisTemplate != null) {
            RedisAtomicLong redisAtomicLong = new RedisAtomicLong(ID_IDX, Objects.requireNonNull(redisTemplate.getConnectionFactory()));
            for (int i = 0; i <= MaxWorkerIdNumberByMode; i++) {
                long andInc = redisAtomicLong.getAndIncrement();
                long result = andInc % (MaxWorkerIdNumberByMode + 1);
                //计数超出上限之后重新计数
                if (andInc >= MaxWorkerIdNumberByMode) {
                    redisAtomicLong.set(andInc % (MaxWorkerIdNumberByMode));
                }
                cacheKey = ID_IDX + result;
                boolean useSuccess = Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(cacheKey, System.currentTimeMillis(), CACHE_TIMEOUT, TimeUnit.SECONDS));
                if (useSuccess) {
                    workerId = (short) result;
                    break;
                }
            }
            if (workerId == -1) {
                throw new RuntimeException(String.format("已尝试生成%d个ID生成器编号, 无法获取到可用编号", MaxWorkerIdNumberByMode + 1));
            }
        } else {
            workerId = (short) new Random().nextInt(MaxWorkerIdNumberByMode);
        }
        log.info("当前ID生成器编号: " + workerId);
        IdGeneratorOptions options = new IdGeneratorOptions(workerId);
        options.WorkerIdBitLength = WorkerIdBitLength;
        YitIdHelper.setIdGenerator(options);
        scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1, threadPoolTaskExecutor.getThreadPoolExecutor().getThreadFactory());
        //提前一分钟续期
        scheduledThreadPoolExecutor.scheduleWithFixedDelay(resetExpire, ScheduleTimeout, ScheduleTimeout, TimeUnit.SECONDS);
    }


    private Runnable resetExpire = () -> {
        //重新设值, 如果Redis被意外清空或者掉线可以把当前编号重新锁定
        redisTemplate.opsForValue().set(cacheKey, System.currentTimeMillis(), CACHE_TIMEOUT, TimeUnit.SECONDS);
    };

    @PreDestroy
    private void onDestroy() {
        //正常关闭时删除当前生成器编号
        if (redisTemplate != null) {
            redisTemplate.delete(cacheKey);
        }
    }
}
