package com.y687.constants;

/**
 * Description
 *
 * @Author bin.yin
 * @createTime 2020/1/19 16:27
 * @Version
 */
public class LogAspectExcution {

    /**
     * 切点为所有@PostMapping，@GetMapping，@RequestMapping
     */
    public  final static String MAPPING_EXCUTION = "@annotation(org.springframework.web.bind.annotation.PostMapping) \\\" +\\n\" +\n" +
            "            \"            \\\"|| @annotation(org.springframework.web.bind.annotation.GetMapping)\\\" +\\n\" +\n" +
            "            \"            \\\"|| @annotation(org.springframework.web.bind.annotation.RequestMapping)";
}
