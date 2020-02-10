package com.company;

import com.company.utils.OsType;
import com.company.utils.OsUtils;

import java.io.IOException;

public class Process5Initializer {

    public void run() {
        Process5 process;

        if (OsUtils.getOsType() == OsType.LINUX) {
            process = new Process5Linux();
            process.run();
        }
        else if (OsUtils.getOsType() == OsType.WINDOWS){
            process = new Process5Windows();
            process.run();
        }
    }
}
