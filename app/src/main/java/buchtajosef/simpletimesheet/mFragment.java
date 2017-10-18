package buchtajosef.simpletimesheet;

import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.drawable.PaintDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.View;

public abstract class mFragment extends Fragment {
    protected MySQLiteHelper myTimeSheetDB;

    public abstract void fillView ();

    public void setBackground (View v) {
        ShapeDrawable.ShaderFactory shaderFactory = new ShapeDrawable.ShaderFactory() {
            @Override
            public Shader resize(int width, int height) {
                float x = 1.7f;
                return new RadialGradient(width/2,(height/x),900.0f,
                        new int[]{
                                ContextCompat.getColor(getContext(), R.color.colorAccent),// Color.parseColor("#06b1dd"),
                                ContextCompat.getColor(getContext(), R.color.colorPrimary)},//Color.parseColor("#03417a")},
                        new float[]{
                                0, 1f},
                        Shader.TileMode.CLAMP);
            }
        };
        PaintDrawable paint = new PaintDrawable();
        paint.setShape(new RectShape());
        paint.setShaderFactory(shaderFactory);
        v.setBackground(paint);

        /*
        new int[]{
                                Color.parseColor("#74b7f2"),
                                Color.parseColor("#4ea1e5"),
                                Color.parseColor("#0a77d5"),
                                Color.parseColor("#0a77d5"),
                                Color.parseColor("#0a77d5"),
                                Color.parseColor("#0a77d5"),
                                Color.parseColor("#0a77d5")}
        GradientDrawable gd = new GradientDrawable();
        gd.setGradientType(GradientDrawable.RADIAL_GRADIENT);
        gd.setColors(new int[]{
                Color.BLACK,
                Color.GREEN,
                Color.BLUE,
                Color.RED
        });
        gd.setGradientRadius(250);
        v.setBackground(gd);*/
    }
}
