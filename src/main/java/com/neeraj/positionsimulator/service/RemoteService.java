package com.neeraj.positionsimulator.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author neeraj on 05/10/19
 * Copyright (c) 2019, VehiclePositionSimulator.
 * All rights reserved.
 */
@FeignClient(url = "${trip-directions-url}", name = "trip-directions")
public interface RemoteService {

    @GetMapping("/directions/file")
    ResponseEntity<Resource> getDirectionsInFile(@RequestParam("source") String source,
                                                 @RequestParam("destination") String destination,
                                                 @RequestParam("vehicleName") String vehicleName);

}
