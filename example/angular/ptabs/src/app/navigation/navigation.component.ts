import { Component, Input, Output, EventEmitter } from '@angular/core';
import { NgFor } from '@angular/common';

export interface NavigationItem {
  id: string;
  label: string;
  tabIndex?: number;
}

@Component({
  selector: 'app-navigation',
  imports: [NgFor],
  templateUrl: './navigation.component.html',
  styleUrl: './navigation.component.css'
})
export class NavigationComponent {
  @Input() items: NavigationItem[] = [];
  @Input() activeSection: string = '';
  @Output() sectionClick = new EventEmitter<{sectionId: string, tabIndex?: number}>();

  onSectionClick(sectionId: string, tabIndex?: number): void {
    this.sectionClick.emit({ sectionId, tabIndex });
  }
} 