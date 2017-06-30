package org.vanch.handheldreader;

import com.handheld.UHF.UhfManager;

import java.util.ArrayList;
import java.util.List;

import cn.pda.serialport.Tools;

/**
 * Created by Mehmet on 30.6.2017.
 */

public class VanchReader extends Thread {

    private boolean mStarted = false;
    private UhfManager mManager = null;
    private ArrayList<TagsReportedListener> mListeners = new ArrayList<>();

    public void setOnTagsReportedListener(TagsReportedListener listener) {
        this.mListeners.add(listener);
    }

    protected void fireTagsReported(List<String> tags) {
        for (TagsReportedListener listener : this.mListeners) {
            listener.onTagsReported(tags);
        }
    }

    public void open() throws UhfReaderException {
        try {
            mManager = UhfManager.getInstance();
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

    public boolean isStarted() {
        return mStarted;
    }

    @Override
    public void run() {
        super.run();

        List<byte[]> epcList;
        ArrayList<String> readEpcList;

        while (isStarted()) {
            // manager.stopInventoryMulti()
            epcList = mManager.inventoryRealTime(); // inventory real time
            readEpcList = new ArrayList<>();
            if (epcList != null && !epcList.isEmpty()) {
                // TODO: play sound
                for (byte[] epc : epcList) {
                    String epcStr = Tools.Bytes2HexString(epc,
                            epc.length);
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
