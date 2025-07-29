import {Component, EventEmitter, Input, OnInit} from '@angular/core';
import {NgForOf, NgIf} from "@angular/common";
import {HttpErrorResponse} from "@angular/common/http";
import {FileService} from "../../file.service";
import {FormsModule} from "@angular/forms";
import {DialogTutorialComponent} from "../../../shared/mat-dialog-tutorial/dialog-tutorial.component";
import {ActivatedRoute, RouterLink} from "@angular/router";
import {ConvertService} from "../../core/services/convert.service";
import {initFlowbite} from "flowbite";
import {MatDialogWindowComponent} from "../../shared/mat-dialog-window/mat-dialog-window.component";

@Component({
  selector: 'app-convertor',
  imports: [
    FormsModule,
    RouterLink,
    NgForOf,
    NgIf,
    MatDialogWindowComponent,
  ],
  templateUrl: './convertor.component.html',
  styleUrl: './convertor.component.css'
})
export class ConvertorComponent implements OnInit{

  constructor(
    private convertService: ConvertService,
    private route: ActivatedRoute) {
  }

  showExcel: boolean = false;

  openExcelPreviewModal(): void {
this.showExcel = true;
  }

  closeExcelPreviewModal(): void {
this.showExcel = false;
  }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.convertId = params.get('id') || '';
    });
        initFlowbite();
        this.onFetchConvert();
    }

  fileName: string = '';
  selectedFile: File | null = null;
  modalOpen = false;
  myTutorials = [
    'Krok 1: Otvorte menu',
    'Krok 2: Kliknite na "Nový projekt"',
    'Krok 3: Vyplňte formulár a uložte'
  ];

  convertId: any = null
  toConvert: any = null
  excelSheet: any[] = [];
  notMapped: any = null

  openModal() {
    this.modalOpen = true;
  }

  closeModal() {
    this.modalOpen = false;
  }

  getNotMappedHeadersCount(): number {
    return this.toConvert.filter((m: any) => m.mappedHeader == null && m.included === true).length;
  }

  onIncludedChange() {
    // Ak potrebujete, tu môžete vykonať ďalšiu logiku pri zmene
    // Po tejto zmene sa automaticky prerátajú hodnoty pri ďalšom volaní getNotMappedHeadersCount()
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

  unmappedHeaders: string[] = ['Dátum narodenia', 'Neznámy stĺpec'];

  previewHeaders = ['Dátum', 'Číslo faktúry', 'Suma', 'Dodávateľ'];
  previewRows = [
    ['2024-06-12', 'INV-001', '240.00 €', 'Slovenská pošta'],
    ['2024-06-15', 'INV-002', '128.00 €', 'Orange Slovensko'],
    ['2024-06-20', 'INV-003', '300.00 €', 'Lidl']
  ];

  expandedHeader: string | null = null;

  toggleExpanded(header: string) {
    this.expandedHeader = this.expandedHeader === header ? null : header;
  }

// Pomocná metóda pre konverziu hlavičky na safe ID (ak sú tam medzery, diakritika atď.)
  slugify(str: string): string {
    return str
      .normalize('NFD')                   // odstraň diakritiku
      .replace(/[\u0300-\u036f]/g, '')
      .toLowerCase()
      .replace(/\s+/g, '-')              // medzery na pomlčky
      .replace(/[^\w\-]+/g, '')          // odstráni špeciálne znaky
      .replace(/\-\-+/g, '-')            // viac pomlčiek → 1
      .replace(/^-+/, '')                // odstráni začiatok -
      .replace(/-+$/, '');               // odstráni koniec -
  }

  getNotMappedHeaders(): string[] {
    return this.toConvert
      .filter((m: any) => !m.mappedHeader && m.included === true)
      .map((m: any) => m.originalHeader);
  }

  onMappedHeaderChange(): void {
    // Môžeš to len nechať takto – Angular už vie prerenderovať UI
    // Alebo si sem dáš nejakú logiku/logovanie/debug
  }

  onRemoveFromConvert(header: string): void {
    this.toConvert = this.toConvert.filter((m: any) => m.originalHeader !== header);
  }

  excelColumns: string[] = [];

  onFetchConvert() {
    this.convertService.getConvertById({
      convertId: this.convertId
    })
      .then(response => {
        console.log(response.data)
        this.toConvert = response.data.mappings
        this.excelSheet = response.data.data
        console.log(this.excelSheet)

        this.excelSheet = JSON.parse(response.data.data);

        console.log(this.excelSheet)
        if (this.excelSheet.length > 0) {
          this.excelColumns = Object.keys(this.excelSheet[0]); // Zoberie hlavičky z prvého záznamu
        }
      })
  }


}
