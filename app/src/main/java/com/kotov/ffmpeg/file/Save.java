package com.kotov.ffmpeg.file;

import android.os.Environment;

import java.io.File;

public class Save {


    private String n;
    private String crateAccount;
    private String createAcountSubDir;

    public Save(String n, String crateAccount, String createAcountSubDir) {
        this.n = n;
        this.crateAccount = crateAccount;
        this.createAcountSubDir = createAcountSubDir;
    }

    /**
     * Create directory ITSmart
     *
     * @return
     */
    private File createDir() {

        File folders = new File(Environment.getExternalStorageDirectory().getAbsolutePath().concat(n));
        if (!folders.exists()) {
            if (folders.mkdir()) {

            }
        }
        return folders;
    }

    private File createAcounts() {
        File folders = new File(createDir() + "/" + crateAccount);
        if (!folders.exists()) {
            if (folders.mkdir()) {

            }
        }
        return folders;
    }

    private File createAcountSubDir() {
        File folders = new File(createAcounts() + "/" + createAcountSubDir);
        if (!folders.exists()) {
            if (folders.mkdir()) {

            }
        }
        return folders;
    }

    public File createFileAccount() {
        if (crateAccount == null) {
            return null;
        } else if (createAcountSubDir == null) {
            createAcounts();
        } else {
            if (createDir().exists()) {
                if (createAcounts().exists()) {
                    return createAcountSubDir();
                } else {
                    return createAcounts();
                }
            } else {
                return createDir();
            }
        }
        return null;
    }

}
