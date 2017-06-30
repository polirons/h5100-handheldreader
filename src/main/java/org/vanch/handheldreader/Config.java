package org.vanch.handheldreader;

import com.handheld.UHF.UhfManager;

/**
 * Created by Mehmet on 30.6.2017.
 */

public class Config {

    int outputPower = 17;
    int workArea = UhfManager.WorkArea_Europe;

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
