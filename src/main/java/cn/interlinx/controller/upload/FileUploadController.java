package cn.interlinx.controller.upload;

import cn.interlinx.utils.util.ResponseUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;

@RestController
public class FileUploadController {

    /**
     * 上传图片
     * @param request
     * @param file enctype="multipart/form-data"
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/api/upload", method = RequestMethod.POST)
    public String upLoad(HttpServletRequest request,
                         @RequestParam("file") MultipartFile file) throws IOException {
        if (!file.isEmpty()) {
            // 上传文件路径
            String path = request.getServletContext().getRealPath("/upload/");
            System.out.println("path = " + path);
            // 上传文件名
            String filename = file.getOriginalFilename();
            File filepath = new File(path, filename);
            // 判断路径是否存在，如果不存在就创建一个
            if (!filepath.getParentFile().exists()) {
                filepath.getParentFile().mkdirs();
            }
            // 将上传文件保存到一个目标文件当中
            File file1 = new File(path + File.separator + filename);
            file.transferTo(file1);

            return ResponseUtils.getResult("200", "上传文件成功", file1.getAbsolutePath());
        } else {
            return ResponseUtils.getResult("4004", "上传文件失败", "");
        }

    }

}
