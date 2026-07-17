package app.scheduler.controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    @PostMapping("/create")
    public String create() {
        return "Create";
    }

    @PostMapping("/delete")
    public String delete() {
        return "Delete";
    }

    @PostMapping("/update")
    public String update() {
        return "Update";
    }

    @PostMapping("/list")
    public String list() {
        return "List";
    }

    @PostMapping("/search")
    public String search() {
        return "Search";
    }

    @PostMapping("/filter")
    public String filter() {
        return "Filter";
    }

    @PostMapping("/sort")
    public String sort() {
        return "Sort";
    }

    @PostMapping("/export")
    public String export() {
        return "Export";
    }

    @PostMapping("/import")
    public String importData() {
        return "Import";
    }

    @PostMapping("/upload")
    public String upload() {
        return "Upload";
    }

    @PostMapping("/download")
    public String download() {
        return "Download";
    }

}
