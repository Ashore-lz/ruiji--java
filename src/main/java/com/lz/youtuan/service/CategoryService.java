package com.lz.youtuan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lz.youtuan.entity.Category;

public interface CategoryService extends IService<Category> {
    public void remove(Long id);
}
