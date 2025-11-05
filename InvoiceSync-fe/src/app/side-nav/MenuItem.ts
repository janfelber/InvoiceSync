export interface MenuItem {
  label: string;
  route?: string;
  features?: string[];
  icon?: string;
  children?: MenuItem[];
}
