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
    private float mStartToolbarPosition; // Toolbar 开始的Y点,最大可移动的距离
    private float mChangeBehaviorPoint;//改变行为的点,由向上,变为向右向上
    private float mChangeLastYMove;//改变行为时的位置
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

        float expandedPercentageFactor =dependency.getTop() / mStartToolbarPosition;//移动的比例,越来越小
        //移动的比例,小于设置的比例,
        if (expandedPercentageFactor < mChangeBehaviorPoint) { //也就是剩余的距离比原本头像的高度小,开始缩小头像
            //计算要移动的比例
            float heightFactor = (mChangeBehaviorPoint - expandedPercentageFactor) / mChangeBehaviorPoint;

            //计算头像宽高的变化
            float heightToSubtract = ((mStartHeight - mFinalHeight) * heightFactor);

            //根据比例缩小头像
            CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) child.getLayoutParams();
            lp.width = (int) (mStartHeight - heightToSubtract);
            lp.height = (int) (mStartHeight - heightToSubtract);
            child.setLayoutParams(lp);

            float distanceXToSubtract = (mStartXPosition - mFinalXPosition) * heightFactor + (child.getHeight() / 2);
            child.setX(mStartXPosition - distanceXToSubtract);
            float distanceYToSubtract = (mStartYPosition - mChangeLastYMove) * (1 - heightFactor);

            if (distanceYToSubtract < (mFinalYPosition - mFinalHeight / 2)) {//位置在最后位置的上面,则设置为最后的位置
                distanceYToSubtract = mFinalYPosition - mFinalHeight / 2;
            }
            child.setY(distanceYToSubtract);
        } else {//向上移动
            float distanceYToSubtract = ((mStartYPosition - mFinalYPosition) * (1f - expandedPercentageFactor)) + mStartHeight / 2;//按比例计算位置

            child.setX(mStartXPosition - mStartHeight / 2);
            child.setY(mStartYPosition - distanceYToSubtract);
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

        if (mStartYPosition == 0) //设置初始的Y点 跟Toolbar一样
            mStartYPosition = mStartToolbarPosition = dependency.getTop();

        if (mFinalYPosition == 0) //最终y点
            mFinalYPosition = dependency.getHeight() / 2;//最终y值 在　toolbar 的中间



        if (mStartXPosition == 0)//起始点
            mStartXPosition = child.getLeft() + (child.getWidth() / 2);

        if (mFinalXPosition == 0)
            mFinalXPosition = dependency.findViewById(R.id.lyt_title).getLeft() + 5;//返回键的右边,也就是ryt的左边

        if (mChangeBehaviorPoint == 0)
            mChangeBehaviorPoint = (mStartHeight - mFinalHeight) * (1 + mFactor) / mStartYPosition;//标记头像高度所占的要位移距离的比例

        if (mChangeLastYMove == 0)
            mChangeLastYMove = (mStartYPosition - mFinalYPosition) * (1f - mChangeBehaviorPoint) + mStartHeight / 2;

    }



}
