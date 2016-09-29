package demo.cq.coordinatorbehaviorexample;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by cqll on 2016/7/29.
 */
public class AvatarImageBehavior extends CoordinatorLayout.Behavior<CircleImageView> {

    private Context mContext;


    private float mStartXPosition; // 头像 刚开始的 X点
    private float mFinalXPosition; // 头像 结束时的 X点

    private float mStartYPosition; // 头像开始的 Y点
    private float mFinalYPosition; // 头像结束的 Y点

    private float mStartHeight; // 开始时 头像的高度
    private float mFinalHeight; // 结束时 头像的高度
    private float mFactor;//越大,向左上移动的时机就越快
    private float mChangeBehaviorPoint;//改变行为的点,由向上,变为向右向上
    public AvatarImageBehavior(Context context, AttributeSet attrs) {
        mContext = context;
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AvatarImageBehavior);
            mFinalHeight = a.getDimension(R.styleable.AvatarImageBehavior_finalHeight, 0);
            mFactor = a.getFloat(R.styleable.AvatarImageBehavior_factor, 0);
            a.recycle();
        }
    }


    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, CircleImageView child, View dependency) {
        return dependency instanceof Toolbar;//依赖toolbar
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, CircleImageView child, View dependency) {
        maybeInitProperties(child, dependency);

        //移动的位置,没有到改变行为的位置
        float position=dependency.getTop()-child.getHeight()/2;
        if(position<0){
            position=0;
        }
        if (position <= mChangeBehaviorPoint) {

            //计算要移动的比例
            float heightFactor = (mChangeBehaviorPoint - position) / mChangeBehaviorPoint;

            //计算头像宽高的变化
            float heightToSubtract = ((mStartHeight - mFinalHeight) * heightFactor);

            //根据比例缩小头像
            CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) child.getLayoutParams();
            lp.width = (int) (mStartHeight - heightToSubtract);
            lp.height = (int) (mStartHeight - heightToSubtract);
            child.setLayoutParams(lp);

            float distanceXToSubtract=mFinalXPosition+(mStartXPosition-mFinalXPosition)*(1-heightFactor);


            //y轴移动头像
            float distanceYToSubtract = (mChangeBehaviorPoint) * (1 - heightFactor);

            if (distanceYToSubtract < mFinalYPosition) {//位置在最后位置的上面,则设置为最后的位置
                distanceYToSubtract = mFinalYPosition;
            }
            if(position==0){
                distanceYToSubtract=mFinalYPosition;
                distanceXToSubtract=mFinalXPosition;
            }

            child.setX(distanceXToSubtract);
            child.setY(distanceYToSubtract);
        } else {//向上移动

            child.setX(mStartXPosition);
            child.setY(position);
            CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) child.getLayoutParams();
            lp.width = (int) mStartHeight;
            lp.height = (int) mStartHeight;
            child.setLayoutParams(lp);
        }
        return true;
    }

    /**
     * 初始化数据
     *
     * @param child      头像
     * @param dependency toolbar
     */
    private void maybeInitProperties(CircleImageView child, View dependency) {

        if (mStartHeight == 0)
            mStartHeight = child.getHeight();

        if (mStartYPosition == 0) //设置初始的Y点
            mStartYPosition  = dependency.getTop();

        if (mFinalYPosition == 0) //最终y点
            mFinalYPosition = (dependency.getHeight()/2-mFinalHeight/2);

        if (mStartXPosition == 0)//起始点
            mStartXPosition = child.getLeft();

        if (mFinalXPosition == 0) {
            mFinalXPosition = dependency.findViewById(R.id.lyt_title).getLeft()-dip2px(mContext,2*((Toolbar)dependency).getTitleMarginTop())+10;//返回键的右边,也就是ryt的左边
        }

        if (mChangeBehaviorPoint == 0)//头像变化的高度,加上改变因子,getTop<this 应该改变行为
            mChangeBehaviorPoint = (mStartHeight - mFinalHeight) * (1 + mFactor);


    }
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


}
