package com.lz.youtuan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lz.youtuan.entity.AddressBook;
import com.lz.youtuan.mapper.AddressBookMapper;
import com.lz.youtuan.service.AddressBookService;
import org.springframework.stereotype.Service;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {

}
