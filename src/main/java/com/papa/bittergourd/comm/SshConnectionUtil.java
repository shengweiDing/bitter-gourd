package com.papa.bittergourd.comm;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.InputStream;

public class SshConnectionUtil {

      public static void executeCommand(String host, String user, String password, String command){
          try {
              JSch jsch = new JSch();
              Session session = jsch.getSession(user, host, 22);
              session.setPassword(password);
              session.setConfig("StrictHostKeyChecking", "no");
              session.connect();
              Channel channel = session.openChannel("exec");
              ((ChannelExec) channel).setCommand(command);
              channel.setInputStream(null);
              ((ChannelExec) channel).setErrStream(System.err);
              InputStream in = channel.getInputStream();
              channel.connect();
              byte[] tmp = new byte[1024];
              while (true) {
                  while (in.available() > 0) {
                      int i = in.read(tmp, 0, 1024);
                      if (i < 0) break;
                      System.out.print(new String(tmp, 0, i));
                  }
                  if (channel.isClosed()) {
                      if (in.available() > 0) continue;
                      System.out.println("exit-status: " + channel.getExitStatus());
                      break;
                  }
                  try {
                      Thread.sleep(1000);
                  } catch (Exception ignored) {
                  }
              }
              channel.disconnect();
              session.disconnect();
          } catch (Exception e) {
              e.printStackTrace();
          }
      }

}
