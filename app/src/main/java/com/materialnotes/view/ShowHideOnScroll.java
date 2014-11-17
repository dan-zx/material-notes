package com.materialnotes.view;

import android.support.v7.app.ActionBar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.materialnotes.R;

import com.shamanland.fab.FloatingActionButton;
import com.shamanland.fab.ScrollDetector;

/**
 * Esconde y muestra un FloatingActionButton y ActionBar cuando una vista que tiene este listener
 * hace scroll hacia arriba o hacia abajo.
 *
 * Basado en <a href="http://bit.ly/10ZFVhL">shamanland/floating-action-button ShowHideOnScroll.java</a>
 *
 * @author Daniel Pedraza Arcega
 */
public class ShowHideOnScroll extends ScrollDetector implements Animation.AnimationListener  {

    private final FloatingActionButton fab;
    private final ActionBar actionBar;

    /**
     * Constructor.
     *
     * @param fab un FloatingActionButton
     * @param actionBar una ActionBar
     */
    public ShowHideOnScroll(FloatingActionButton fab, ActionBar actionBar) {
        super(fab.getContext());
        this.fab = fab;
        this.actionBar = actionBar;
    }

    /** {@inheritDoc} */
    @Override
    public void onScrollDown() {
        if (!areViewsVisible()) {
            fab.setVisibility(View.VISIBLE);
            actionBar.show();
            animateFAB(R.anim.floating_action_button_show);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onScrollUp() {
        if (areViewsVisible()) {
            fab.setVisibility(View.GONE);
            actionBar.hide();
            animateFAB(R.anim.floating_action_button_hide);
        }
    }

    /** @return {@code true} si estan visibles el FAB y la ActionBar; {@code false} de otra forma. */
    private boolean areViewsVisible() {
        return fab.getVisibility() == View.VISIBLE && actionBar.isShowing();
    }

    /**
     * Anima el FAB según la animación dada.
     *
     * @param anim una animación.
     */
    private void animateFAB(int anim) {
        Animation a = AnimationUtils.loadAnimation(fab.getContext(), anim);
        a.setAnimationListener(this);
        fab.startAnimation(a);
        setIgnore(true);
    }

    /** {@inheritDoc} */
    @Override
    public void onAnimationStart(Animation animation) {
        // Nada
    }

    /** {@inheritDoc} */
    @Override
    public void onAnimationEnd(Animation animation) {
        setIgnore(false);
    }

    /** {@inheritDoc} */
    @Override
    public void onAnimationRepeat(Animation animation) {
        // Nada
    }
}