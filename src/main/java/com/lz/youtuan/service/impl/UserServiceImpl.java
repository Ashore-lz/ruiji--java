package com.lz.youtuan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lz.youtuan.entity.AddressBook;
import com.lz.youtuan.entity.User;
import com.lz.youtuan.mapper.AddressBookMapper;
import com.lz.youtuan.mapper.UserMapper;
import com.lz.youtuan.service.AddressBookService;
import com.lz.youtuan.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

}
