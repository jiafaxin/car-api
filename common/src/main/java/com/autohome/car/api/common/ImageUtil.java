package com.autohome.car.api.common;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;

public class ImageUtil {

    /**
     * 获取图片完整路径
     *
     * @return
     */
    public static String getFullImagePath(String path,String oldDir,String newDir) {
        if (StringUtils.isBlank(path)) {
            return "";
        }
        return String.format("%s%s", getImageDomain(path), (StringUtils.isNotBlank(oldDir) || StringUtils.isNotBlank(newDir)) ?
                path.replace("~", "") :
                path.replace(oldDir, newDir).replace("~", ""));
    }
    public static String getFullImagePathByPrefix(String path, String prefix) {
        if (StringUtils.isBlank(path)) {
            return "";
        }
        // 获取图片名称和目录
        String[] parts = path.replace("~", "").replace("\\", "/").split("/");
        String fileName = parts[parts.length - 1];
        String directoryName = "";
        for (int i = 0; i < parts.length - 1; i++) {
            directoryName += parts[i] + "/";
        }
        // 去除末尾的斜杠
        directoryName = StringUtils.stripEnd(directoryName, "/");
        // 获取图片域名
        String imageDomain = getImageDomain(path);
        // 获取图片名称起始索引
        int startIndex = path.contains("/cardfs/") ? fileName.indexOf("autohomecar__") : (fileName.indexOf("_") + 1);
        // 去掉已有前缀
        fileName = startIndex > 0 ? fileName.substring(startIndex) : fileName;
        // 去除前面的斜杠
        prefix = StringUtils.stripStart(prefix, "/");
        // 拼接完整的图片路径
        return String.format("%s%s/%s%s", imageDomain, directoryName, prefix == null ? "" : prefix, fileName);
    }

    public static String getFullImagePath(String path) {
        if (StringUtils.isBlank(path)) {
            return "";
        }
        path = path.replace("~", "");
        return String.format("%s%s", getImageDomain(path), path);
    }

    /**
     *
     * @param path 路径
     * @param replaceFirst 替换优先
     * @return 路径
     */
    public static String getFullImagePathNew(String path, boolean replaceFirst) {
        if (StringUtils.isBlank(path)) {
            return Strings.EMPTY;
        }
        return StringUtils.contains(path, "~") && replaceFirst ? getFullImagePath(path) : getFullImagePathWithoutReplace(path);
    }


    public static String getFullImagePathWithoutReplace(String path) {
        if (StringUtils.isBlank(path)) {
            return path;
        }
        return String.format("%s%s", getImageDomain(path), path.replace("~", ""));
    }

    /**
     * 通过图片路径获取图片域名
     * @param path
     * @return
     */
    private static String getImageDomain(String path) {
        if (StringUtils.isBlank(path)) {
            return path;
        }
        int r = 0, b = 0;
        while ((r += 4) < path.length()) { b ^= path.charAt(r); }
        b %= 2;
        return String.format("https://car%s.autoimg.cn", path.contains("/cardfs/") ? String.valueOf(b + 2): "0");
    }

}
