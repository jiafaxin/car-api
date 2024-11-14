package com.autohome.car.api.common;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.text.MessageFormat;

/**
 * 图片拼接
 * @className: CarSettings
 * @description:
 */
public class CarSettings {
    private CarSettings() {
    }

    private static CarSettings single = null;

    public static CarSettings getInstance() {
        if (single == null) {
            synchronized (CarSettings.class) {
                if (single == null) {
                    single = new CarSettings();
                }
            }
        }
        return single;
    }

    private String imageDomain = "https://car0.autoimg.cn";
    private String ImageDomain;
    private String webPhysicsPath;

    public static CarSettings getSingle() {
        return single;
    }

    public static void setSingle(CarSettings single) {
        CarSettings.single = single;
    }

    public String getWebPhysicsPath() {
        return webPhysicsPath;
    }

    public void setWebPhysicsPath(String webPhysicsPath) {
        this.webPhysicsPath = webPhysicsPath;
    }

    /// <summary>
    /// 获取带域名的图片路径
    /// </summary>
    /// <param name="imgurl">图片相对路径</param>
    /// <returns></returns>
    public String GetImageDomain(String imgurl) {
        if (!StringUtils.isNotEmpty(imgurl))
            return imgurl;
        char[] str = imgurl.toLowerCase().substring(imgurl.lastIndexOf('/') + 1).toCharArray();
        int r = 0, b = 0;
        while ((r += 4) < imgurl.length()) {
            b ^= str[r];
        }
        b %= 2;
        return MessageFormat.format("https://car{0}.autoimg.cn{1}", Integer.toString(b), imgurl);
    }

    /// <summary>
    /// 通过图片路径获取图片域名
    /// </summary>
    /// <param name="path">图片路径</param>
    /// <returns>图片域名</returns>
    private String GetImageDomainNew(String path) {
        if (!StringUtils.isNotEmpty(path))
            return path;
        int r = 0, b = 0;
        while ((r += 4) < path.length()) { b ^= path.charAt(r); }
        b %= 2;
        return String.format("https://car%s.autoimg.cn", path.contains("/cardfs/") ? String.valueOf(b + 2): "0");
    }

    /// <summary>
    /// 获取图片完整路径
    /// </summary>
    /// <param name="path">图片路径</param>
    /// <returns>图片完整路径</returns>
    public String GetFullImagePath(String path) {
        if (!StringUtils.isNotEmpty(path)||path.startsWith("https://"))
            return path;
        return MessageFormat.format("{0}{1}", GetImageDomainNew(path), path.replace("~", ""));
    }

    /// <summary>
    /// 获取图片完整路径(通过前缀改变图片尺寸)
    /// </summary>
    /// <param name="path">图片路径</param>
    /// <param name="prefix">前缀（图片尺寸区分）</param>
    /// <returns>图片完整路径</returns>
    public String GetFullImagePathByPrefix(String path, String prefix) {
        if (!StringUtils.isNotEmpty(prefix)) {
            prefix = "";
        }
        if (!StringUtils.isNotEmpty(path))
            return path;
        File tempFile = new File(path.trim());
        // 图片名称
        String fileName = tempFile.getName();
        // 图片目录
//        String directoryName = Path.GetDirectoryName(path).Replace("~", "").Replace("\\", "/");
        String directoryName = tempFile.getParent().replace("~", "").replace("\\", "/");
        // 图片域名
        String _imageDomain = GetImageDomainNew(path);
        // 图片名称起始索引
        int startIndex = path.contains("/cardfs/") ? fileName.indexOf("autohomecar__") : (fileName.indexOf("_") + 1);
        // 去掉已有前缀
        fileName = startIndex > 0 ? fileName.substring(startIndex, fileName.length()) : fileName;

        return MessageFormat.format("{0}{1}/{2}{3}", _imageDomain, directoryName, prefix, fileName);
    }

    /// <summary>
    /// 获取图片完整路径(通过文件目录改变图片尺寸)
    /// 调用： GetFullImagePathByPrefix(path,"/oa/","/500a/")
    /// </summary>
    /// <param name="path">图片路径</param>
    /// <param name="oldDir">原文件目录</param>
    /// <param name="newDir">新文件目录</param>
    /// <returns>图片完整路径</returns>
    public String GetFullImagePathByDirectory(String path, String oldDir, String newDir) {
        if (!StringUtils.isNotEmpty(oldDir)) {
            oldDir = "";
        }
        if (!StringUtils.isNotEmpty(newDir)) {
            newDir = "";
        }
        if (!StringUtils.isNotEmpty(path))
            return path;
        return MessageFormat.format("{0}{1}", GetImageDomainNew(path), (!StringUtils.isNotEmpty(oldDir) || !StringUtils.isNotEmpty(newDir)) ? path.replace("~", "") : path.replace(oldDir, newDir).replace("~", ""));
    }

}
