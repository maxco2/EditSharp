package io.gitcafe.maxco292.editsharp;

import android.util.Log;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by maxco on 2016/3/24.
 */
public class SSFTPsync {
    public static void sshSftp(
            String ip, String user, String psw ,int port,
            String remoteDirectory,
            String LocalFilePath,
            String Remotefilename
    ) throws Exception{
        Session session;
        Channel channel = null;


        JSch jsch = new JSch();


        //连接服务器，采用默认端口
        if(port <=0) session = jsch.getSession(user, ip);
        else{
            //采用指定的端口连接服务器
            session = jsch.getSession(user, ip ,port);
        }

        //如果服务器连接不上，则抛出异常
        if (session == null) {
            throw new Exception("session is null");
        }

        //设置登陆主机的密码
        session.setPassword(psw);//设置密码
        //设置第一次登陆的时候提示，可选值：(ask | yes | no)
        session.setConfig("StrictHostKeyChecking", "no");
        //设置登陆超时时间
        session.connect(30000);

        try {
            //创建sftp通信通道
            channel = session.openChannel("sftp");
            channel.connect(1000);
            ChannelSftp sftp = (ChannelSftp) channel;


            //进入服务器指定的文件夹
            sftp.cd(remoteDirectory);

            //列出服务器指定的文件列表
//            Vector v = sftp.ls("*.txt");
//            for(int i=0;i<v.size();i++){
//                System.out.println(v.get(i));
//            }

            //以下代码实现从本地上传一个文件到服务器，如果要实现下载，对换以下流就可以了
            OutputStream outstream = sftp.put(Remotefilename);
            InputStream instream = new FileInputStream(new File(LocalFilePath));

            byte b[] = new byte[1024];
            int n;
            while ((n = instream.read(b)) != -1) {
                outstream.write(b, 0, n);
            }

            outstream.flush();
            outstream.close();
            instream.close();
            Log.d("SSHSYNC","sync succ");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.disconnect();
            assert channel != null;
            channel.disconnect();
        }
    }
    public static void sshSftpDOWN(
            String ip, String user, String psw ,int port,
            String remoteDirectory,
            String LocalFilePath,
            String Remotefilename
    ) throws Exception{
        Session session;
        Channel channel = null;


        JSch jsch = new JSch();


        if(port <=0){
            //连接服务器，采用默认端口
            session = jsch.getSession(user, ip);
        }else{
            //采用指定的端口连接服务器
            session = jsch.getSession(user, ip ,port);
        }

        //如果服务器连接不上，则抛出异常
        if (session == null) {
            throw new Exception("session is null");
        }

        //设置登陆主机的密码
        session.setPassword(psw);//设置密码
        //设置第一次登陆的时候提示，可选值：(ask | yes | no)
        session.setConfig("StrictHostKeyChecking", "no");
        //设置登陆超时时间
        session.connect(30000);

        try {
            //创建sftp通信通道
            channel = session.openChannel("sftp");
            channel.connect(1000);
            ChannelSftp sftp = (ChannelSftp) channel;


            //进入服务器指定的文件夹
            sftp.cd(remoteDirectory);

            //列出服务器指定的文件列表
//            Vector v = sftp.ls("*.txt");
//            for(int i=0;i<v.size();i++){
//                System.out.println(v.get(i));
//            }
            File fi=new File(LocalFilePath);


            //以下代码实现从本地上传一个文件到服务器，如果要实现下载，对换以下流就可以了
            InputStream instream = sftp.get(Remotefilename);
            OutputStream outstream= new FileOutputStream(fi);
            //InputStream instream = new FileInputStream(new File(LocalFilePath));

            byte b[] = new byte[1024];
            int n;
            while ((n = instream.read(b)) != -1) {
                outstream.write(b, 0, n);
            }

            outstream.flush();
            outstream.close();
            instream.close();
            Log.d("SSHSYNC","download sync succ");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.disconnect();
            assert channel != null;
            channel.disconnect();
        }
    }
}
