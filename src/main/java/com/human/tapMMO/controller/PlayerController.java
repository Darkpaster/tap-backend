package com.human.tapMMO.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

//@RestController
//@RequestMapping("/player")
//@RequiredArgsConstructor
public class PlayerController {
//    private final CampaignService campaignService;
//
//    @GetMapping("/")
//    public ResponseEntity<List<CampaignDTO>> getCampaigns(@RequestParam String advertiserId, int size, int page) {
//        return ResponseEntity.status(HttpStatus.OK).body(campaignService.getCampaignsByAdvertiser(UUID.fromString(advertiserId), size, page));
//    }
//
//    @PostMapping("/")
//    public ResponseEntity<CampaignDTO> createCampaign(@RequestParam String advertiserId, @RequestBody CampaignDTO campaignData) {
//        return ResponseEntity.status(HttpStatus.CREATED).body(campaignService.createCampaign(UUID.fromString(advertiserId), campaignData));
//    }
//
//    @GetMapping("/{campaignId}")
//    public ResponseEntity<CampaignDTO> getCampaign(@PathVariable String advertiserId, @PathVariable String campaignId) {
//        return ResponseEntity.status(HttpStatus.OK).body(campaignService.getCampaignById(UUID.fromString(campaignId)).orElseThrow(
//                () -> new NoSuchElementException("Не найдена кампания.")
//        ));
//    }
//
//    @PutMapping("/{campaignId}")
//    public ResponseEntity<CampaignDTO> updateCampaign(@PathVariable String advertiserId, @PathVariable String campaignId, @RequestBody CampaignDTO campaignData) {
//        return ResponseEntity.status(HttpStatus.OK).body(campaignService.
//                updateCampaign(UUID.fromString(advertiserId), UUID.fromString(campaignId), campaignData));
//    }
//
//    @DeleteMapping("/{campaignId}")
//    public ResponseEntity<String> deleteCampaign(@PathVariable String advertiserId, @PathVariable String campaignId) {
//        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(campaignService.deleteCampaign(UUID.fromString(campaignId)));
//    }
}
