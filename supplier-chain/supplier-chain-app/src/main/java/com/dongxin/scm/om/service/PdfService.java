package com.dongxin.scm.om.service;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@Service
public class PdfService {

    public void reqPdf(String url, String fileName, String fileUrl, String suffix) {
        HttpRequest req = HttpUtil.createGet(url);
        HttpResponse response = req
                .contentType("application/json")
                .execute();
        InputStream in = null;
        OutputStream out = null;
        try {
            //得到输入流
            in = response.bodyStream();
            //得到输出流
//            File file1 = new File("D:\\contractPDF");
//            if (
//                !file1.exists()
//            ) {
//                file1.mkdirs();
//            }
            File file = new File(fileUrl + fileName + suffix);
            if (!file.exists()) {
                file.createNewFile();
            }
            out = new FileOutputStream(file, true);
            int i;//从输入流读取一定数量的字节，返回 0 到 255 范围内的 int 型字节值
            while ((i = in.read()) != -1) {
                out.write(i);
            }
        } catch (
                FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
