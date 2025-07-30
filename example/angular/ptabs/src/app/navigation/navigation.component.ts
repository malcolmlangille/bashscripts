import { Component, Input, Output, EventEmitter, signal, OnInit, OnDestroy } from '@angular/core';
import { NgFor } from '@angular/common';
import { TranslationService } from '../services/translation.service';
import { Subscription } from 'rxjs';

export interface NavigationItem {
  id: string;
  label: string;
  labelFr: string;
  tabIndex?: number;
}

@Component({
  selector: 'app-navigation',
  imports: [NgFor],
  templateUrl: './navigation.component.html',
  styleUrl: './navigation.component.css'
})
export class NavigationComponent implements OnInit, OnDestroy {
  @Input() items: NavigationItem[] = [];
  @Input() activeSection: string = '';
  @Output() sectionClick = new EventEmitter<{sectionId: string, tabIndex?: number}>();

  // Language toggle - true for English, false for French
  protected readonly isEnglish = signal(true);
  private languageSubscription?: Subscription;

  constructor(private translationService: TranslationService) {}

  ngOnInit(): void {
    console.log('NavigationComponent ngOnInit - loading translations');
    
    // Load initial translations
    this.translationService.loadTranslations('en').subscribe(() => {
      console.log('English translations loaded');
    });
    
    this.translationService.loadTranslations('fr').subscribe(() => {
      console.log('French translations loaded');
      // After French translations are loaded, set default language
      this.translationService.setLanguage('en');
    });

    // Subscribe to language changes
    this.languageSubscription = this.translationService.getLanguageChangeObservable().subscribe(language => {
      console.log('Language changed to:', language);
      this.isEnglish.set(language === 'en');
    });
  }

  ngOnDestroy(): void {
    if (this.languageSubscription) {
      this.languageSubscription.unsubscribe();
    }
  }

  onSectionClick(sectionId: string, tabIndex?: number): void {
    this.sectionClick.emit({ sectionId, tabIndex });
  }

  toggleLanguage(): void {
    console.log('Toggle language clicked. Current language:', this.translationService.getCurrentLanguage());
    const newLanguage = this.isEnglish() ? 'fr' : 'en';
    console.log('Switching to language:', newLanguage);
    this.translationService.setLanguage(newLanguage);
  }

  getLabel(item: NavigationItem): string {
    // Map item IDs to translation keys
    const translationMap: { [key: string]: string } = {
      'key-features': 'navigation.keyFeatures',
      'technical-implementation': 'navigation.technicalImplementation',
      'notes': 'navigation.notes',
      'project-overview': 'navigation.projectOverview',
      'technical-architecture': 'navigation.technicalArchitecture',
      'user-interface-design': 'navigation.userInterfaceDesign',
      'component-integration': 'navigation.componentIntegration',
      'future-enhancements': 'navigation.futureEnhancements'
    };

    const translationKey = translationMap[item.id];
    const result = translationKey ? this.translationService.getTranslation(translationKey) : item.label;
    console.log(`Getting label for ${item.id}: ${result}`);
    return result;
  }

  getNavigationTitle(): string {
    const result = this.translationService.getTranslation('navigation.title');
    console.log('Navigation title:', result);
    return result;
  }

  getLanguageToggleText(): string {
    const result = this.translationService.getTranslation('common.languageToggle');
    console.log('Language toggle text:', result);
    return result;
  }
} 