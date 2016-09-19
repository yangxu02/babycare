package com.linkx.babycare.di.components;

import android.app.Activity;
import android.app.Application;
import com.linkx.babycare.di.ActivityScope;
import com.linkx.babycare.di.modules.ActivityModule;
import dagger.Component;

@ActivityScope
@Component(dependencies = Application.class, modules = ActivityModule.class)
public interface ActivityComponent {
    Activity activity();
}
