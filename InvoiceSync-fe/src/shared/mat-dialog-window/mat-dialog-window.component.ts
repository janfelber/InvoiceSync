import {Component, Inject} from '@angular/core';
import { CommonModule } from "@angular/common";
import {
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogContent,
  MatDialogRef,
  MatDialogTitle
} from "@angular/material/dialog";
import {MatButton} from "@angular/material/button";
import {FormsModule} from "@angular/forms";

interface InputData {
  label: string;
  value: any;
  required: boolean;
  type: string;
  placeholder: string;
}

@Component({
    selector: 'app-mat-dialog-window',
    imports: [
        CommonModule,
        MatDialogContent,
        MatDialogActions,
        MatButton,
        MatDialogTitle,
        FormsModule
    ],
    templateUrl: './mat-dialog-window.component.html',
    styleUrl: './mat-dialog-window.component.css'
})
export class MatDialogWindowComponent {
  constructor(
    public dialogRef: MatDialogRef<MatDialogWindowComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {}

  close(result: boolean): void {
    this.dialogRef.close(result);
  }

  save(): void {
    const result = this.data.inputs.map((input: InputData) => ({
      label: input.label,
      value: input.value
    }));

    this.dialogRef.close(result);
  }
}
