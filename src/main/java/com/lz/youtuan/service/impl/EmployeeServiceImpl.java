package com.lz.youtuan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lz.youtuan.entity.Employee;
import com.lz.youtuan.mapper.EmployeeMapper;
import com.lz.youtuan.service.EmployeeService;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
}
