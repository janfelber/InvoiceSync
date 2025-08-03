import {Component, OnInit} from '@angular/core';
import {NgForOf, NgIf} from "@angular/common";
import {FormsModule} from "@angular/forms";
import {ActivatedRoute} from "@angular/router";
import {DataTransferService} from "../../core/services/data-transfer.service";
import {initFlowbite} from "flowbite";

@Component({
  selector: 'data-transfer-mapping',
  imports: [
    FormsModule,
    NgForOf,
    NgIf
  ],
  templateUrl: './data-transfer-mapping.component.html',
  styleUrl: './data-transfer-mapping.component.css'
})
export class DataTransferMappingComponent implements OnInit{

  constructor(
    private convertService: DataTransferService,
    private route: ActivatedRoute) {
  }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.convertId = params.get('id') || '';
    });
        initFlowbite();
        this.onFetchConvert();
    }

  fileName: string = '';
  convertId: any = null
  toConvert: any = null
  excelSheet: any[] = [];
  excelColumns: string[] = [];

  mappedHeadersOptions = [
    "Číslo",
    "Oraganizácia",
    "Vystavenie",
    "Splatnosť",
    "Zdaniteľné plnenie",
    "Úhrada dátum",
    "Suma bez DPH",
    "Úhrada suma",
    "Celkom suma",
    "IČO",
    "IČDPH",
    "Objednávky",
  ];

  onFetchConvert() {
    this.convertService.getConvertById({
      convertId: this.convertId
    })
      .then(response => {
        this.toConvert = response.data.mappings
        this.excelSheet = response.data.data

        this.excelSheet = JSON.parse(response.data.data);

        if (this.excelSheet.length > 0) {
          this.excelColumns = Object.keys(this.excelSheet[0]);
        }
      })
  }

  scrollToHeader(header: string): void {
    const id = 'header-' + this.slugify(header);
    const el = document.getElementById(id);

    if (el) {
      el.scrollIntoView({ behavior: 'smooth', block: 'center' });

      el.classList.add('pulse-error-once');
      setTimeout(() => el.classList.remove('pulse-error-once'), 2000);

    }
  }

  removeHeader(headerName: string): void {
    const target = this.toConvert.find((t:any) => t.originalHeader === headerName);
    if (target) {
      target.included = false;
      this.onIncludedChange();
    }
  }

  onSaveMapping(): void {
    this.convertService.saveMapping(this.convertId, this.toConvert).then(response => {
      this.onFetchConvert();
    });
  }

  slugify(str: string): string {
    return str
      .normalize('NFD')
      .replace(/[\u0300-\u036f]/g, '')
      .toLowerCase()
      .replace(/\s+/g, '-')
      .replace(/[^\w\-]+/g, '')
      .replace(/\-\-+/g, '-')
      .replace(/^-+/, '')
      .replace(/-+$/, '');
  }

  getNotMappedHeaders(): string[] {
    return this.toConvert
      .filter((m: any) => !m.mappedHeader && m.included === true)
      .map((m: any) => m.originalHeader);
  }

  selectOption(test: any, option: string) {
    test.mappedHeader = option;
    test.dropdownOpen = false;
    this.onMappedHeaderChange();
  }

  onInputBlur(test: any) {
    setTimeout(() => {
      test.dropdownOpen = false;
    }, 150);
  }

  onInputChange(test: any) {
    test.dropdownOpen = true;
  }

  getNotMappedHeadersCount(): number {
    return this.toConvert.filter((m: any) => m.mappedHeader == null && m.included === true).length;
  }

  onIncludedChange() {
  }

  onMappedHeaderChange(): void {
  }

  filteredMappedHeaders() {
    return this.mappedHeadersOptions;
  }

}
