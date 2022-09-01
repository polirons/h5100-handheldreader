package org.h5100.handheldreader;

import com.uhf.api.cls.Reader;

/**
 * Created by Mehmet on 30.6.2017.
 */

public class Config {

    int outputPower = 33;
    int workArea = Reader.Region_Conf.RG_EU3.value();

    public int getOutputPower() {
        return outputPower;
    }

    public void setOutputPower(int outputPower) {
        this.outputPower = outputPower;
    }

    public int getWorkArea() {
        return workArea;
    }

    public void setWorkArea(int workArea) {
        this.workArea = workArea;
    }
}
