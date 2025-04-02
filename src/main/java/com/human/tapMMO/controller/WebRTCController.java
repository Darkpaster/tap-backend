package com.human.tapMMO.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/webrtc")
public class WebRTCController {
    @GetMapping("/ice-servers")
    public ResponseEntity<List<IceServer>> getIceServers() {
        List<IceServer> iceServers = List.of(
                new IceServer("stun:stun.l.google.com:19302")
        );
        return ResponseEntity.ok(iceServers);
    }

}

@Data
@AllArgsConstructor
class IceServer {
    private String urls;
}