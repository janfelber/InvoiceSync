import {TutorialItem} from './TutorialItem';

export interface TutorialCategory {
  id: string;
  label: string;
  description: string;
  tutorials: Record<string, TutorialItem[]>; // keyed by app name
}

export const tutorialCategories: TutorialCategory[] = [
  {
    id: 'accounts',
    label: 'Chart of Accounts',
    description: 'How to export your chart of accounts from accounting software and import it into InvoiceSync.',
    tutorials: {
      Pohoda: [
        {
          src: 'assets/tutorials/test1.mp4',
          thumbnail: 'assets/SmallLogo.svg',
          text: 'Pohoda: Export účtov',
          thumbnailText: 'Pohoda: Export účtov',
        },
        {
          src: 'assets/tutorials/test2.mp4',
          thumbnail: 'https://source.unsplash.com/600x300/?spreadsheet,accounting',
          text: 'Money: Import do systému',
        },
      ],
      Omega: [
        {
          src: 'assets/tutorials/pohoda1.mp4',
          thumbnail: 'https://source.unsplash.com/600x300/?office,documents',
          text: 'Omega: Export účtov',
          thumbnailText: 'Omega: Export účtov',
        }
      ]
    }
  },
  {
    id: 'invoices',
    label: 'Invoices',
    description: 'Learn how to create, manage and export invoices.',
    tutorials: {}
  },
  {
    id: 'receipts',
    label: 'Receipts',
    description: 'Managing cash and card receipts — export, numbering and more.',
    tutorials: {}
  },
  {
    id: 'getting-started',
    label: 'Getting Started',
    description: 'First steps — setting up your company, adding accounts and creating your first document.',
    tutorials: {}
  }
];
