package com.lz.youtuan.dto;


import com.lz.youtuan.entity.Setmeal;
import com.lz.youtuan.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
