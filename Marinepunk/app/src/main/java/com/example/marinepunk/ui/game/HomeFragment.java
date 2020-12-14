package com.example.marinepunk.ui.game;

import android.Manifest;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.example.marinepunk.R;
import com.example.marinepunk.help.TableManager;
import com.example.marinepunk.viewmodel.ApplicationViewModel;

import net.glxn.qrgen.android.QRCode;
import net.glxn.qrgen.core.image.ImageType;

import java.util.UUID;

public class HomeFragment extends Fragment {

    private ApplicationViewModel applicationViewModel;
    private TextView oneShip;
    private TextView twoShip;
    private TextView threeShip;
    private TextView fourShip;
    private Dialog qrShow, qrScan;
    private CodeScanner scanner;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        applicationViewModel =
                new ViewModelProvider(requireActivity()).get(ApplicationViewModel.class);
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        TableManager.createField(requireActivity(), view, R.id.field,
            applicationViewModel.getOwnField().getValue());
        TableLayout field = (TableLayout) view.findViewById(R.id.field);
        applicationViewModel.getOwnField().observe(getViewLifecycleOwner(), state -> {
            TableManager.fillField(requireActivity(), field,
                applicationViewModel.getOwnField().getValue(), false);
        });
        applicationViewModel.setFieldClickListenerCreate(field);

        EditText textView = view.findViewById(R.id.createGameCodeInput);

        Button createGameCodeGenerate = (Button) view.findViewById(R.id.createGameCodeGenerate);
        createGameCodeGenerate.setOnClickListener(v -> {
            textView.setText(UUID.randomUUID().toString());
        });

        Button createGameCodeCopy = (Button) view.findViewById(R.id.createGameCodeCopy);
        createGameCodeCopy.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) requireActivity()
                .getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Generated code copied",
                textView.getText().toString());
            clipboard.setPrimaryClip(clip);
        });

        Button createGameCodeCreate = (Button) view.findViewById(R.id.createGameCodeCreate);
        createGameCodeCreate.setOnClickListener(v -> {
            applicationViewModel.initiateGameState(textView.getText().toString(), requireActivity());
        });

        Button createGameCodeConnect = (Button) view.findViewById(R.id.createGameCodeConnect);
        createGameCodeConnect.setOnClickListener(v -> {
            applicationViewModel.connectToGame(textView.getText().toString(), requireActivity());
        });

        Button createQR = (Button) view.findViewById(R.id.createQR);
        createQR.setOnClickListener(v -> {
            String code = textView.getText().toString();
            if (!code.isEmpty()) {
                qrShow = new Dialog(requireActivity());
                // Context of delete dialog
                qrShow.setContentView(R.layout.qr_dialog);
                int width = WindowManager.LayoutParams.MATCH_PARENT;
                int height = WindowManager.LayoutParams.WRAP_CONTENT;
                qrShow.getWindow().setLayout(width, height);

                // Show dialog
                qrShow.show();

                Bitmap codeImage = QRCode.from(code).to(ImageType.JPG)
                        .withSize(750, 750).bitmap();
                // ByteArrayOutputStream codeStream = QRCode.from(code).stream();
                ImageView qrImage = (ImageView) qrShow.findViewById(R.id.qrImage);
                qrImage.setImageBitmap(codeImage);

                Button button_close = qrShow.findViewById(R.id.closeQrButton);
                button_close.setOnClickListener(v1 -> { qrShow.dismiss(); });
            }
        });

        Button createQRScan = (Button) view.findViewById(R.id.createQRConnect);
        createQRScan.setOnClickListener(v -> {
            qrScan = new Dialog(requireActivity());
            // Context of delete dialog
            qrScan.setContentView(R.layout.qr_scan_layout);

            int width = WindowManager.LayoutParams.MATCH_PARENT;
            int height = WindowManager.LayoutParams.MATCH_PARENT;
            qrScan.getWindow().setLayout(width, height);
            qrScan.show();

            CodeScannerView scannerView = qrScan.findViewById(R.id.scanner_view);
            scanner = new CodeScanner(requireActivity(), scannerView);
            scanner.setDecodeCallback(result -> {
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireActivity(), "Scanned!", Toast.LENGTH_SHORT).show();
                    textView.setText(result.getText());
                    qrScan.dismiss();
                    scanner.stopPreview();
                });
            });
            scannerView.setOnClickListener(v1 -> {
                if (ContextCompat.checkSelfPermission(
                    requireActivity(), Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    scanner.startPreview();
                    scanner.setErrorCallback(error -> {
                        scanner.stopPreview();
                        qrScan.dismiss();
                    });
                } else {
                    ActivityCompat.requestPermissions(
                        requireActivity(), new String[]{Manifest.permission.CAMERA}, 101
                    );
                }
            });
            qrScan.setOnDismissListener(dialog -> {
                scanner.stopPreview();
            });
        });

        oneShip   = (TextView) view.findViewById(R.id.rightShipsOne);
        twoShip   = (TextView) view.findViewById(R.id.rightShipsTwo);
        threeShip = (TextView) view.findViewById(R.id.rightShipsThree);
        fourShip  = (TextView) view.findViewById(R.id.rightShipsFour);

        applicationViewModel.getCheckField().observe(getViewLifecycleOwner(), ships -> {
            boolean rightShipsQuantity =
                    ships[0] == 4 && ships[1] == 3 && ships[2] == 2 && ships[3] == 1;
            createGameCodeCreate.setEnabled(rightShipsQuantity);
            oneShip.  setText(ships[0] != 4 ? "4" : "✓");
            twoShip.  setText(ships[1] != 3 ? "3" : "✓");
            threeShip.setText(ships[2] != 2 ? "2" : "✓");
            fourShip. setText(ships[3] != 1 ? "1" : "✓");
        });

        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 101:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (qrScan != null && scanner != null) {
                        scanner.startPreview();
                    } else {
                        qrScan.dismiss();
                    }
                } else {
                    if (scanner != null) scanner.stopPreview();
                    if (qrScan != null)  qrScan.dismiss();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}