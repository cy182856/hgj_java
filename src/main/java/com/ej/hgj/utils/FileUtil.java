package com.ej.hgj.utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * 文件读取工具类
 */
public class FileUtil {

    /**
     * 读取文件内容，作为字符串返回
     * @throws IOException
     */
    public static String readFileAsString(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new FileNotFoundException(filePath);
        }

        if (file.length() > 1024 * 1024 * 1024) {
            throw new IOException("File is too large");
        }

        StringBuilder sb = new StringBuilder((int) (file.length()));
        // 创建字节输入流
        FileInputStream fis = null;
        try {
	        fis = new FileInputStream(filePath);
	        // 创建一个长度为10240的Buffer
	        byte[] bbuf = new byte[10240];
	        // 用于保存实际读取的字节数
	        int hasRead = 0;
	        while ( (hasRead = fis.read(bbuf)) > 0 ) {
	            sb.append(new String(bbuf, 0, hasRead));
	        }
        }finally {
            try {
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
        return sb.toString();
    }

    /**
     * 根据文件路径读取byte[] 数组
     */
    public static byte[] readFileByBytes(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new FileNotFoundException(filePath);
        } else {
            ByteArrayOutputStream bos = new ByteArrayOutputStream((int) file.length());
            BufferedInputStream in = null;

            try {
                in = new BufferedInputStream(new FileInputStream(file));
                short bufSize = 1024;
                byte[] buffer = new byte[bufSize];
                int len1;
                while (-1 != (len1 = in.read(buffer, 0, bufSize))) {
                    bos.write(buffer, 0, len1);
                }

                byte[] var7 = bos.toByteArray();
                return var7;
            } finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    if (bos != null) {
                    	bos.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


	public static void outPutImage(String fileSavePath, HttpServletResponse response){
		BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
        	bis = new BufferedInputStream(new FileInputStream(new File(fileSavePath)));
			bos = new BufferedOutputStream(response.getOutputStream());

	        String mimeType = "";
	        String suffix = fileSavePath.substring(fileSavePath.lastIndexOf(".") + 1).toLowerCase();
	        switch (suffix){
		        case "jpg":
		        case "jpeg":
		            mimeType = "image/jpeg";
		        	break;
		        case "png":
		        	mimeType = "image/png";
		        	break;
		        case "ico":
		        	mimeType = "image/x-icon";
		        	break;
		        case "gif":
		            mimeType = "image/gif";
		        	break;
		        case "bmp":
		            mimeType = "image/bmp";
		        	break;
		        case "svg":
		            mimeType = "image/svg+xml";
		        	break;
		        default:
		            mimeType = "image/jpeg";
	        }



           // 设置响应的类型格式为图片格式
           response.setContentType(mimeType);
           //禁止图像缓存。
//           response.setHeader("Pragma", "no-cache");
//           response.setHeader("Cache-Control", "no-cache");
//           response.setDateHeader("Expires", 0);

			byte[] buff = new byte[8192];
			int bytesRead;
			while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
				bos.write(buff, 0, bytesRead);
			}
			bos.flush();

        }catch(Exception e) {

        } finally {
        	try {
        		if (bis!= null)
        			bis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				if (bos!= null)
					bos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
    }

	// 获取流文件
	public static void inputStreamToFile(InputStream ins, File file) {
		OutputStream os = null;
		try {
			os = new FileOutputStream(file);
			int bytesRead = 0;
			byte[] buffer = new byte[8192];
			while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
				os.write(buffer, 0, bytesRead);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (os != null)
					os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				if (ins != null)
					ins.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	//删除文件
	public static boolean deleteFile(String fileName) {
        File file = new File(fileName);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                System.out.println("删除单个文件" + fileName + "成功！");
                return true;
            } else {
                System.out.println("删除单个文件" + fileName + "失败！");
                return false;
            }
        } else {
            System.out.println("删除单个文件失败：" + fileName + "不存在！");
            return true;
        }
    }

	/**
	 * 删除目录及目录下的文件
	 *
	 * @param dir：要删除的目录的文件路径
	 * @return 目录删除成功返回true，否则返回false
	 */
	public static boolean deleteDirectory(String dir) {
		// 如果dir不以文件分隔符结尾，自动添加文件分隔符
		if (!dir.endsWith(File.separator))
			dir = dir + File.separator;
		File dirFile = new File(dir);
		// 如果dir对应的文件不存在，或者不是一个目录，则退出
		if ((!dirFile.exists()) || (!dirFile.isDirectory())) {
			return false;
		}
		boolean flag = true;
		// 删除文件夹中的所有文件包括子目录
		File[] files = dirFile.listFiles();
		for (int i = 0; i < files.length; i++) {
			// 删除子文件
			if (files[i].isFile()) {
				flag = deleteFile(files[i].getAbsolutePath());
				if (!flag)
					break;
			}
			// 删除子目录
			else if (files[i].isDirectory()) {
				flag = deleteDirectory(files[i].getAbsolutePath());
				if (!flag)
					break;
			}
		}
		if (!flag) {
			return false;
		}
		// 删除当前目录
		if (dirFile.delete()) {
			return true;
		} else {
			return false;
		}
	}

	public static void fileDownload(HttpServletRequest request, HttpServletResponse response, String filePath){
		OutputStream out = null;
		InputStream inputStream = null;
		File file = new File(filePath);
		try {
			// 以流的形式下载文件。
			inputStream = new BufferedInputStream(new FileInputStream(file.getPath()));
			byte[] buffer = new byte[inputStream.available()];
			inputStream.read(buffer);
			out = new BufferedOutputStream(response.getOutputStream());
			//response.reset();
			//response.setContentType("text/html;charset=UTF-8");
			//request.setCharacterEncoding("UTF-8");
			//response.setHeader("Content-Disposition", "attachment;");
			out.write(buffer);
			out.flush();
		} catch (IOException ex) {
			ex.printStackTrace();
		}finally {
			if(out!=null) {
				try {
					out.close();
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
			if(inputStream!=null) {
				try {
					inputStream.close();
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

}
