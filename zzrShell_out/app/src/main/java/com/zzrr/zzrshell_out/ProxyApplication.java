package com.zzrr.zzrshell_out;

import android.app.Application;
import android.content.Context;
import android.util.ArrayMap;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.time.LocalDate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import dalvik.system.DexClassLoader;
import dalvik.system.PathClassLoader;

public class ProxyApplication extends Application {
    private String TAG = "ProxyApplication";
    private String dexFileName;
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        byte[] srcdex = null;
        try {
            srcdex = getDex();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String cachePath = getCacheDir().getAbsolutePath();

        dexFileName = cachePath + "/classes3.dex";
        Log.i(TAG,"extract source dex to " + dexFileName);
        File dexFile = new File(dexFileName);
        if(dexFile.exists()){
            dexFile.delete();
        }
        if(!dexFile.exists()){
            try {
                Log.i(TAG,"execute there");
                dexFile.createNewFile();
                writedex(srcdex);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        Object currentActivityThread = RefInvoke.invokeStaticMethod("android.app.ActivityThread",
                "currentActivityThread",new Class[]{},new Object[]{});
        String packageName = this.getPackageName();
        ArrayMap mPackages = (ArrayMap) RefInvoke.getFieldOjbect(
                "android.app.ActivityThread", currentActivityThread,
                "mPackages");
        WeakReference wr = (WeakReference) mPackages.get(packageName);
        PathClassLoader dexClassLoader = new PathClassLoader(dexFileName,generateLibPath(),getClassLoader());
        RefInvoke.setFieldOjbect("android.app.LoadedApk", "mClassLoader",
                wr.get(), dexClassLoader);
        Log.i(TAG,"new DexClassLoader: "+dexClassLoader);
        try {
            Log.d(TAG,"begin load");
            Class clazz1 = dexClassLoader.loadClass("com.zzrr.testshell.MainActivity");
            Class clazz2 = dexClassLoader.loadClass("com.zzrr.testshell.SecondActivity");
            Log.d(TAG,"load success");
            Log.d(TAG,clazz1.toString());
            Log.d(TAG,clazz2.toString());
            //Intent intent = new Intent(this,clazz1);
            //startActivity(intent);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private String generateLibPath(){
        //File.pathSeparator;
        String libPath = getClassLoader().toString();
        String[] strs = libPath.split("nativeLibraryDirectories=\\[");
        Log.d(TAG,strs[1]);
        int index = strs[1].toString().indexOf("]");
        Log.d(TAG,strs[1].toString().substring(0,index));
        return strs[1].toString().substring(0,index).replace(" ","").replace(",",":");

    }

    private void writedex(byte[] dstbytes) throws IOException {
        File file = new File(dexFileName);
        Log.i(TAG,"write success!!!");
        Log.i(TAG,"dex length :"+dstbytes.length);
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(dstbytes);
        fileOutputStream.flush();
        fileOutputStream.close();
    }


    private byte[] getDex() throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Log.d(TAG,"sourcedir :" + this.getApplicationInfo().sourceDir);
        ZipInputStream zipInputStream = new ZipInputStream(new BufferedInputStream(new FileInputStream(this.getApplicationInfo().sourceDir)));
        while(true){
            ZipEntry zipEntry = zipInputStream.getNextEntry();
            //Log.i(TAG,zipEntry.getName());
            if(zipEntry == null){
                zipInputStream.close();
                Log.i(TAG,": not found srcdex");
                return null;
            }
            else if(zipEntry.getName().equals("assets/classes.dex")){
                Log.i(TAG,"found src dex");
                byte[] bytes = new byte[1024];
                while(true){
                    int i = zipInputStream.read(bytes);
                    if(i != -1){
                        byteArrayOutputStream.write(bytes,0,i);
                    }
                    else{
                        bytes = byteArrayOutputStream.toByteArray();
                        Log.i(TAG,"there length is :"+bytes.length);
                        zipInputStream.closeEntry();
                        zipInputStream.close();
                        return bytes;
                    }


                }
            }



        }
    }
}
