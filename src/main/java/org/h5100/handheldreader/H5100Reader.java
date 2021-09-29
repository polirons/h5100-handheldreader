package org.h5100.handheldreader;

import com.handheld.uhfr.UHFRManager;
import com.uhf.api.cls.Reader;

import java.util.ArrayList;
import java.util.List;

import cn.pda.serialport.Tools;

/**
 * Created by Mehmet on 30.6.2017.
 */

public class H5100Reader extends Thread {

    private boolean mStarted = false;
    private UHFRManager mManager = null;
    private ArrayList<TagsReportedListener> mListeners = new ArrayList<>();

    public void setOnTagsReportedListener(TagsReportedListener listener) {
        this.mListeners.add(listener);
    }

    private void fireTagsReported(List<String> tags) {
        for (TagsReportedListener listener : this.mListeners) {
            listener.onTagsReported(tags);
        }
    }

    public void open() throws UhfReaderException {
        try {
            mManager = UHFRManager.getInstance();
            if (mManager == null) {
                throw new UhfReaderException("Reader could not open!");
            }
            mStarted = true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void close() throws UhfReaderException {
        try {
            if (mManager != null) {
                mManager.close();
            }
            mStarted = false;
        } catch (Exception ex) {
            throw new UhfReaderException(ex.getMessage());
        }
    }

    public void configure(final Config config) throws UhfReaderException {
        if (mManager == null) {
            open();
        }

        // band = 4;
        // Fre = 867,9Mhz Eksik burada kaldık.

        //UHfData.UHfGetData.SetRfPower()

        /*
        *(byte) band = 4
        * (byte) MaxFre = 14
        *(byte) MinFre = 0
        * (byte) Power = 30
        *
        * int result = UHfGetData.SetUhfInfo((byte) band, (byte) MaxFre,
					(byte) MinFre, (byte) Power);
        *
        * */

        // [0] is readPower, [1] is writePower
        // we need to set only readPower to tune read antenna powers
        int[] powers = mManager.getPower();
        powers[0] = config.getOutputPower();
	powers[1] = config.getOutputPower();

        if (mManager.setPower(powers[0], powers[1]) != Reader.READER_ERR.MT_OK_ERR) {
            // TODO: report special error for each READER_ERR enum values
            throw new UhfReaderException("Output power could not set!");
        }

        //MainActivity.mUhfrManager.setGen2session(isMulti);
        mManager.setGen2session(false);

        /*if (mManager.setRegion(Reader.Region_Conf.valueOf(config.workArea)) != Reader.READER_ERR.MT_OK_ERR) {
            // TODO: report special error for each READER_ERR enum values
            throw new UhfReaderException("Work area could not set!");
        }*/
    }

    public boolean isStarted() {
        return mStarted;
    }

    @Override
    public void run() {
        super.run();

        List<Reader.TAGINFO> epcList;
        ArrayList<String> readEpcList;

        while (isStarted()) {
            // manager.stopInventoryMulti()
            epcList = mManager.tagInventoryByTimer((short) 50);
            readEpcList = new ArrayList<>();
            if (epcList != null && !epcList.isEmpty()) {
                // TODO: play sound
                for (Reader.TAGINFO epc : epcList) {
                    String epcStr = Tools.Bytes2HexString(epc.EpcId,
                            epc.EpcId.length);
                    readEpcList.add(epcStr);
                }
            }
            if (readEpcList.size() > 0) {
                fireTagsReported(readEpcList);
                readEpcList.clear();
            }
            try {
                Thread.sleep(40);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

}
