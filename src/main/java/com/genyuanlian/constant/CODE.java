package com.genyuanlian.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by qaa on 2017/9/12.
 * * @author zhaoqing
 * 返回codec常量
 */
public class CODE {
    public static final int SUCCESS = 0;//成功插入数据库
    public static final int DEPARTMENT_ERROR = -1;//部门填写有误
    public static final int DATE_ERROR = -2;//日期填写有误
    public static final int NAME_ERROR = -3;//姓名填写有误
    public static final int REASON_ERROR = -4;//加班缘由填写有误
    public static final int DURATION_ERROR = -5;//加班时长填写有误
    public static final int PLACE_ERROR = -6;//加班地点填写有误
    public static final int SYSTEM_ERROR = -7;//系统异常
    public static final int USERNAME_ERROR = -8; //用户名存在
    public static final int USERNAME_NOT_ERROR = -9; //用户名不存在
    public static final int RECORD_NOT_CONFIRMED = -10;  //记录还没有确认过
    public static final int RECORD_NOT_RECORDED = -11;  //记录还没有提交
    public static final int BALANCE_NOT_ENOUGH = -12;  //用户余额不足
    public static final int PLAN_NOT_VERIFY = -13;  //计划没有提交验证数据
    public static final int PLAN_SPONSOR_NOT_CONFIRM = -14;  //计划还有赞助人没有确认
    public static final int PLAN_WITNESS_NOT_CONFIRM = -15;  //计划还有见证人没有确认
    public static final int PLAN_WITNESS_NOT_JUDGE = -16;  //计划还有见证人没有评判
    public static final int PLAN_SPONSOR_CONFIRMED = -17;  //计划有赞助人已经确认过，不能被删除
    public static final int PLAN_WITNESS_JUDGED = -18;  //计划有见证人已经做出评判，不能再上传验证图片
    public static final int USER_WITHDRAW_FAIL = -19;  //用户提现失败
    public static final int PLAN_SPONSOR_EXIST = -20;  //计划用户已经赞助了
    public static final int PLAN_WITNESS_EXIST = -21;  //计划用户已经见证了
    public static final int PLAN_NO_SPONSOR_OR_WITNESS = -22;   //计划没有赞助人或者见证人
    public static final int PLAN_NO_VALID_WITNESS = -23;   //计划没有有效的见证人

    public static final Map<String, Integer> CODEMAP = new HashMap<String, Integer>();

    static {
        CODEMAP.put("department", DEPARTMENT_ERROR);
        CODEMAP.put("date", DATE_ERROR);
        CODEMAP.put("name", NAME_ERROR);
        CODEMAP.put("reason", REASON_ERROR);
        CODEMAP.put("duration", DURATION_ERROR);
        CODEMAP.put("place", PLACE_ERROR);
    }

}
