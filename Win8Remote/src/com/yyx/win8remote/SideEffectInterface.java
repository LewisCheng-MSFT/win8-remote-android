package com.yyx.win8remote;

// 副作用接口
// 何谓副作用？就是发生一件事情的时候，顺便发生的另一件事
public interface SideEffectInterface {
	// 有些手势会导致电脑进入Metro界面，这时也要相应把应用切换为Metro模式
	public void switch2MetroMode();
	
	// 长按的时候，需要手机短促震动一下
	public void longPressVibrate();
}
