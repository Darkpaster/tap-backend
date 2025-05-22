package com.human.tapMMO.config;

import com.human.tapMMO.runtime.game.Logger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ShutdownHandler {

    private final Logger chatLogger;

    @EventListener
    public void onApplicationShutdown(ContextClosedEvent event) {
        log.info("Application shutdown detected, flushing chat messages to files...");
        chatLogger.forceFlushAllToFile();
    }
}