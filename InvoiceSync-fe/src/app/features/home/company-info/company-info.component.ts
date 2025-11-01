import { Component, Input, OnInit } from '@angular/core';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {CompanyService} from "../../../core/services/company.service";
import ApexCharts from 'apexcharts';
import {StatsService} from "../../../core/services/stats.service";
import {StatsResponse} from "../../../core/models/stats-response";
import {initFlowbite} from "flowbite";
import {NgIf} from "@angular/common";

@Component({
  selector: 'app-company-info',
  imports: [
    ReactiveFormsModule,
    FormsModule,
    NgIf
  ],
  templateUrl: './company-info.component.html',
  styleUrl: './company-info.component.css'
})
export class CompanyInfoComponent implements OnInit {
  @Input() companyId!: any | null;

  loadingCompany = false;

  company: any = {};
  editedCompany: any = {};
  stats: StatsResponse | null = null;

  constructor(
    private companyService: CompanyService,
    private statsService: StatsService
  ) {
  }

  ngOnInit(): void {
    initFlowbite();
    this.onFetchStats();
    this.onFetchCompany();
  }

  renderChart()  {
    const options = {
      chart: {
        height: 420,
        type: 'area',
        fontFamily: 'Inter, sans-serif',
        foreColor: '#6b7280',
        toolbar: {
          show: false
        },
        animations: {
          enabled: true,
          easing: 'easeOutCubic',
          speed: 1200,
          animateGradually: {
            enabled: true,
            delay: 150
          },
          dynamicAnimation: {
            enabled: true,
            speed: 500
          }
        }
      },
      fill: {
        type: 'gradient',
        gradient: {
          shadeIntensity: 1,
          opacityFrom: 0.4,
          opacityTo: 0.05,
          stops: [0, 90, 100]
        }
      },
      dataLabels: {
        enabled: false
      },
      tooltip: {
        style: {
          fontSize: '14px',
          fontFamily: 'Inter, sans-serif',
        },
      },
      grid: {
        show: false,
      },
      series: [
        {
          name: 'Revenue',
          data: [6356, 6218, 6156, 6526, 6356, 6256, 6056],
          color: '#1a56db'
        },
        {
          name: 'Revenue (previous period)',
          data: [6556, 6725, 6424, 6356, 6586, 6756, 6616],
          color: '#fdba8c'
        }
      ],
      xaxis: {
        categories: ['01 Feb', '02 Feb', '03 Feb', '04 Feb', '05 Feb', '06 Feb', '07 Feb'],
        labels: {
          show: false
        },
        axisBorder: {
          show: false
        },
        axisTicks: {
          show: false
        },
        crosshairs: {
          show: false
        }
      },
      yaxis: {
        show: true,
        labels: {
          style: {
            colors: ['#6b7280'],
            fontSize: '14px',
            fontWeight: 500,
          },
        },
      },
      stroke: {
        width: 6,
        curve: 'smooth'
      },
      legend: {
        fontSize: '14px',
        fontWeight: 500,
        fontFamily: 'Inter, sans-serif',
        labels: {
          colors: ['#6b7280']
        },
        itemMargin: {
          horizontal: 10
        }
      },
      responsive: [
        {
          breakpoint: 1024,
          options: {
            xaxis: {
              labels: {
                show: false
              }
            }
          }
        }
      ]
    };

    if (document.getElementById("area-chart") && typeof ApexCharts !== 'undefined') {
      const chart = new ApexCharts(document.getElementById("area-chart"), options);
      chart.render();
    }
  }


  onFetchCompany() {
    this.loadingCompany = true;
    const delay = (ms: number) => new Promise(resolve => setTimeout(resolve, ms));

    Promise.all([
      this.companyService.getCompanyById(
        {
          companyId: this.companyId
        }
      ),
      delay(700)
    ])
      .then(([response]) => {
        this.company = response.data;
        console.log(this.company)
      })
      .finally(() => {
        this.loadingCompany = false;

        setTimeout(() => {
          this.renderChart();
        }, 0);
      });
  }

  onFetchStats() {
    this.statsService.getBasicStatsForCompany({ companyId: this.companyId })
      .then(response => {
        this.stats = response.data;
        console.log(this.stats)
      });
  }

  editCompany(): void {
    if (!this.company?.id) return;

    this.companyService.updateCompany(
      {
        companyId: this.company.id,
        company: this.editedCompany
      }
    )
      .then((updatedCompany) => {
        this.company = updatedCompany;
        console.log(this.editedCompany)
        this.onFetchCompany()
      })
      .catch((error) => {
        console.error("Chyba pri aktualizácii spoločnosti", error);
      });
  }

  getInitials(name: string): string {
    if (!name) return '';
    const words = name.trim().split(' ');
    if (words.length === 1) {
      return words[0].substring(0, 2).toUpperCase();
    }
    return (words[0][0] + words[1][0]).toUpperCase();
  }

}
