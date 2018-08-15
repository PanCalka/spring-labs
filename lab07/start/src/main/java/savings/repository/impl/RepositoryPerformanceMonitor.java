package savings.repository.impl;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Aspect
@Component
public class RepositoryPerformanceMonitor {

    private static final Logger log = LoggerFactory.getLogger(RepositoryPerformanceMonitor.class);

    @Pointcut("execution(* savings.repository.*Repository.*(..))")
    public void anyRepositoryMethodExecution() {}

    @Around("anyRepositoryMethodExecution()")
    public Object monitor(ProceedingJoinPoint joinPoint) throws Throwable {
        StopWatch stopWatch = new StopWatch(nameOf(joinPoint));
        stopWatch.start();
        try {
            return joinPoint.proceed();
        } finally {
            stopWatch.stop();
            log.info(stopWatch.shortSummary());
        }
    }

    private String nameOf(ProceedingJoinPoint joinPoint) {
        Signature signature = joinPoint.getSignature();
        return signature.getDeclaringType().getSimpleName() + "." + signature.getName();
    }

}
