import { TestBed } from "@angular/core/testing";
import { MatSnackBar, MatSnackBarModule } from "@angular/material/snack-bar";
import { NotificationService } from "./notification.service";
import { LoggerService } from "./logger.service";

describe("NotificationService", () => {
  let service: NotificationService;
  let snackBarSpy: jasmine.SpyObj<MatSnackBar>;
  let loggerSpy: jasmine.SpyObj<LoggerService>;

  beforeEach(() => {
    const snackSpy = jasmine.createSpyObj("MatSnackBar", ["open"]);
    const logSpy = jasmine.createSpyObj("LoggerService", [
      "log",
      "error",
      "warn",
    ]);

    TestBed.configureTestingModule({
      imports: [MatSnackBarModule],
      providers: [
        NotificationService,
        { provide: MatSnackBar, useValue: snackSpy },
        { provide: LoggerService, useValue: logSpy },
      ],
    });

    service = TestBed.inject(NotificationService);
    snackBarSpy = TestBed.inject(MatSnackBar) as jasmine.SpyObj<MatSnackBar>;
    loggerSpy = TestBed.inject(LoggerService) as jasmine.SpyObj<LoggerService>;
  });

  it("deve ser criado", () => {
    expect(service).toBeTruthy();
  });

  it("deve apresentar um snackbar de informação quando show for chamado", () => {
    const message = "Test Message";
    service.show(message);

    expect(loggerSpy.log).toHaveBeenCalledWith(
      'Notificação ["INFO"]:',
      message,
    );
    expect(snackBarSpy.open).toHaveBeenCalledWith(
      message,
      "OK",
      jasmine.objectContaining({ panelClass: ["snackbar-default"] }),
    );
  });

  it("deve apresentar um snackbar de erro quando showError for chamado", () => {
    const message = "Error Message";
    service.showError(message);

    expect(loggerSpy.error).toHaveBeenCalledWith(
      'Notificação ["ERRO"]:',
      message,
    );
    expect(snackBarSpy.open).toHaveBeenCalledWith(
      message,
      "Close",
      jasmine.objectContaining({ panelClass: ["snackbar-error"] }),
    );
  });

  it("deve apresentar um snackbar de aviso quando showWarning for chamado", () => {
    const message = "Warning Message";
    service.showWarning(message);

    expect(loggerSpy.warn).toHaveBeenCalledWith(
      'Notificação ["AVISO"]:',
      message,
    );
    expect(snackBarSpy.open).toHaveBeenCalledWith(
      message,
      "Close",
      jasmine.objectContaining({ panelClass: ["snackbar-warning"] }),
    );
  });

  it("deve apresentar um snackbar de sucesso quando showSuccess for chamado", () => {
    const message = "Success Message";
    service.showSuccess(message);

    expect(loggerSpy.log).toHaveBeenCalledWith(
      'Notificação ["SUCESSO"]:',
      message,
    );
    expect(snackBarSpy.open).toHaveBeenCalledWith(
      message,
      "Close",
      jasmine.objectContaining({ panelClass: ["snackbar-success"] }),
    );
  });
});
