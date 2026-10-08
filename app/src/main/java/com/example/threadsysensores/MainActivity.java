package com.example.threadsysensores;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private Button btnIniciarThread;
    private TextView tvEstadoThread;
    private TextView tvDatosSensor;

    // Componentes del Sensor
    private SensorManager sensorManager;
    private Sensor rotationSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializar vistas
        btnIniciarThread = findViewById(R.id.btnIniciarThread);
        tvEstadoThread = findViewById(R.id.tvEstadoThread);
        tvDatosSensor = findViewById(R.id.tvDatosSensor);

        // 1. Configuración del Sensor (Vector de rotación)
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
            if (rotationSensor == null) {
                // Alternativa común en caso de no disponer de vector de rotación en emulador
                rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            }
        }

        // 2. Operación asíncrona mediante Thread
        btnIniciarThread.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvEstadoThread.setText("Estado: Procesando en segundo plano...");
                btnIniciarThread.setEnabled(false);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            // Simulación de una tarea pesada de 4 segundos
                            Thread.sleep(4000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        // Retornar al Hilo Principal (UI Thread) para actualizar vistas
                        tvEstadoThread.post(new Runnable() {
                            @Override
                            public void run() {
                                tvEstadoThread.setText("Estado: ¡Tarea completada exitosamente!");
                                btnIniciarThread.setEnabled(true);
                                Toast.makeText(MainActivity.this, "Proceso terminado", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).start();
            }
        });
    }

    // Métodos del ciclo de vida para optimizar batería y recursos del sensor
    @Override
    protected void onResume() {
        super.onResume();
        if (rotationSensor != null) {
            sensorManager.registerListener(this, rotationSensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    // Callbacks del SensorEventListener
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.values.length >= 3) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            String info = String.format("Eje X: %.3f\nEje Y: %.3f\nEje Z: %.3f", x, y, z);
            tvDatosSensor.setText(info);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Manejo de cambio de precisión si se requiere
    }
}