import {Component, computed, signal} from '@angular/core';
import {RouterLink, RouterLinkActive} from "@angular/router";
import {NgIf} from "@angular/common";
import {FormBuilder, ReactiveFormsModule, Validators} from "@angular/forms";

@Component({
  selector: 'app-contact-form',
  imports: [
    RouterLink,
    RouterLinkActive,
    NgIf,
    ReactiveFormsModule
  ],
  templateUrl: './contact-form.component.html',
  styleUrl: './contact-form.component.css'
})
export class ContactFormComponent {
  sending = signal(false);
  sent = signal(false);
  serverError = signal<string | null>(null);

  topics = [
    { value: 'support', label: 'Technická podpora' },
    { value: 'sales', label: 'Obchod / cenová ponuka' },
    { value: 'billing', label: 'Fakturácia' },
    { value: 'other', label: 'Iné' },
  ];

  form = this.fb.group({
    name: ['', [Validators.required, Validators.minLength(2)]],
    email: ['', [Validators.required, Validators.email]],
    phone: [''],
    topic: ['support', Validators.required],
    message: ['', [Validators.required, Validators.minLength(10)]],
    consent: [false, Validators.requiredTrue],
  });

  canSubmit = computed(() => this.form.valid && !this.sending());

  constructor(private fb: FormBuilder) {}

  async submit() {
    this.serverError.set(null);
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.sending.set(true);

    // simulácia requestu
    await new Promise(r => setTimeout(r, 1200));

    // TODO: nahraď za reálny API call
    const ok = true;
    if (ok) {
      this.sent.set(true);
      this.form.reset({ topic: 'support', consent: false });
    } else {
      this.serverError.set('Niečo sa pokazilo. Skúste znova, prosím.');
    }
    this.sending.set(false);
  }

  fieldInvalid(ctrl: string) {
    const c = this.form.get(ctrl);
    return !!c && c.touched && c.invalid;
  }

}
