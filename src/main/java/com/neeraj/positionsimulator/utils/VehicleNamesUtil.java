package com.neeraj.positionsimulator.utils;

import org.apache.commons.text.WordUtils;

/**
 * @author neeraj on 22/09/19
 * Copyright (c) 2019, VehiclePositionSimulator.
 * All rights reserved.
 */
public class VehicleNamesUtil {

    public static String prettifyName(String vehicleName) {
        return WordUtils.capitalizeFully(vehicleName.replaceAll("_", " "));
    }
}
