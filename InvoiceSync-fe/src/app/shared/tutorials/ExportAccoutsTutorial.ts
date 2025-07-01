import {TutorialItem} from "./TutorialItem";

export type AccountsTutorialsByApp = Record<string, TutorialItem[]>;

export const accountsTutorialsByApp: AccountsTutorialsByApp = {
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
    {
      src: 'assets/tutorials/aaa.mp4',
      thumbnail: 'https://source.unsplash.com/600x300/?spreadsheet,accounting',
      text: 'Money: Import do systému',
    }
  ],
  Omega: [
    {
      src: 'assets/tutorials/pohoda1.mp4',
      thumbnail: 'https://source.unsplash.com/600x300/?office,documents',
      text: 'Pohoda: Úvod',
      thumbnailText: 'Omega: Export účtov',
    }
  ]
};
