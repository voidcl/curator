package com.nickchen.util;

import com.nickchen.enumm.CommentProcessStatusEnum;
import lombok.Data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;

import static com.nickchen.enumm.CommentProcessStatusEnum.*;

/**
 * 阅读进展报告
 *
 * 使用：
 *  修改 PROJECT_ROOT_PATH 为项目根路径，运行 main 方法即可。
 *
 * @author nickChen
 * create on 2018-07-28
 */
public class ProcessTree {
    private static List<String> filterFiles = new ArrayList<>();
    private static int level = -1;
    private static ProcessStatus processStatus = new ProcessStatus();

    private static final String PROJECT_ROOT_PATH = "e:/curator";

    private static void initFilterFiles() {
        String[] fileName = new String[]{".git", ".", ".gitignore", ".idea", "target",
                "NOTICE", "LICENSE", "DEPENDENCIES", "site", "test"};
        filterFiles.addAll(Arrays.asList(fileName));
    }

    public static void main(String[] args) {
        initFilterFiles();
        ProcessTree.readAllFiles(PROJECT_ROOT_PATH);
        printProcessStatus();
    }

    private static void readAllFiles(String path) {
        File f = new File(path);
        level++;
        if (!f.exists()) {
            System.out.println("No such a file or directory!");
            return;
        }
        File[] flist = f.listFiles();
        if (flist == null) {
            System.out.println(f.getName());
            return;
        }
        Arrays.stream(flist)
                .sorted()
                .forEach(ftmp -> {
                    if (checkDirOrJavaFileAndFilterFiles(ftmp)) return;
                    for (int i = 0; i < level; i++) {
                        System.out.print("│  ");
                    }
                    String tag;
                    if (ftmp.getName().endsWith(".java") && (tag = parseFileComment(ftmp))!=null) {
                        System.out.println("├── " + ftmp.getName() + " " + tag);
                    } else {
                        System.out.println("├── " + ftmp.getName());
                    }
                    if (ftmp.isDirectory()) {
                        readAllFiles(ftmp.getAbsolutePath());
                    }
                });
        level--;
    }
    private static boolean checkDirOrJavaFileAndFilterFiles(File file) {
        String fileName = file.getName();
        return filterFiles.contains(fileName)
                || !(fileName.endsWith(".java")/*是Java文件*/
                || !fileName.contains(".")/*是文件夹，这里api没有找到判断是否是文件夹的*/
        );
    }

    private static void printProcessStatus() {
        System.out.println("====== print process status start ======");
        System.out.println("==== comment process start =====");
        Map<CommentProcessStatusEnum, Integer> commentProcessStatus = processStatus.getCommentProcessStatus();
        commentProcessStatus.forEach((key, value) -> System.out.println(key.getDesc() + " : " + value));
        System.out.println("===== comment process end =======");
        System.out.println("======= print process status end =======");
    }


    private static String parseFileComment(File javaFile) {
        String res = null;
        try(BufferedReader br = new BufferedReader(new FileReader(javaFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("//[")) {
                    //[$1 nick 2018-07-28]
                    res = parseCustomTag(trimExtra(line, "//[", "]"));
                    break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * 解析自定义的 tag
     * 如阅读进展tag：$1 nick 2018-07-28
     */
    private static String parseCustomTag(String tag) {
        return parseCustom$Tag(tag);
    }

    /**
     * 解析自定义的 tag
     * [$1 nick 2018-07-28]
     */
    private static String parseCustom$Tag(String tag) {
        String[] splitBySpace = tag.split(" ");
        // TODO 参数验证
        String process = splitBySpace[0];
        CommentProcessStatusEnum status = CommentProcessStatusEnum.getByStatus(Integer.valueOf(process.substring(1)));
        String pie;
        if (status == null) {
            System.out.println("parse error process" + process);
            return null;
        }
        switch (status) {
            case TODO:
                pie = "◁";break;
            case DOING:
                pie = "☀";break;
            case ALMOST:
                pie = "☼";break;
            case FINISH:
                pie = "☚";break;
            default: pie = "☚";
        }
        commentCount(status);
        String author = splitBySpace[1];
        String time = splitBySpace[2];
        return pie + " <" + author + ">";
    }

    private static void commentCount(CommentProcessStatusEnum status) {
        processStatus.getCommentProcessStatus().compute(status, (s, v) ->{
            if (v == null) {
                return 1;
            }
            return v+1;
        });
    }


    private static String trimExtra(String src, String extra) {
        return trimExtra(src, extra, extra);
    }
    /**
     * 去除前后存在的多余字符
     */
    private static String trimExtra(String src, String extStart, String extEnd) {
        if (src == null) return null;
        if (src.startsWith(extStart)) {
            src = src.substring(extStart.length());
        }
        if (src.endsWith(extEnd)) {
            src = src.substring(0, src.length()-extEnd.length());
        }
        return src;
    }
}
@Data
class ProcessStatus {
    private Map<CommentProcessStatusEnum, Integer> commentProcessStatus = new HashMap<>();
}

