import { Injectable } from '@angular/core';
import { NavigationItem } from '../navigation/navigation.component';
import { TranslationService } from './translation.service';

@Injectable({
  providedIn: 'root'
})
export class SectionService {

  constructor(private translationService: TranslationService) {}

  /**
   * Extracts navigation items from sections with IDs in the DOM
   */
  extractNavigationItems(): NavigationItem[] {
    const sections = [
      // Top sections (outside tabs)
      { 
        id: 'key-features', 
        translationKey: 'navigation.keyFeatures'
      },
      { 
        id: 'technical-implementation', 
        translationKey: 'navigation.technicalImplementation'
      },
      { 
        id: 'notes', 
        translationKey: 'navigation.notes'
      },

      // Details tab sections only
      { 
        id: 'project-overview', 
        translationKey: 'navigation.projectOverview',
        tabIndex: 0 
      },
      { 
        id: 'technical-architecture', 
        translationKey: 'navigation.technicalArchitecture',
        tabIndex: 0 
      },
      { 
        id: 'user-interface-design', 
        translationKey: 'navigation.userInterfaceDesign',
        tabIndex: 0 
      },
      { 
        id: 'component-integration', 
        translationKey: 'navigation.componentIntegration',
        tabIndex: 0 
      },
      { 
        id: 'future-enhancements', 
        translationKey: 'navigation.futureEnhancements',
        tabIndex: 0 
      }

      // History tab sections removed - not shown in navigation
    ];

    // Filter to only include sections that actually exist in the DOM
    return sections.filter(section => {
      const element = document.getElementById(section.id);
      return element !== null;
    }).map(section => ({
      id: section.id,
      label: this.translationService.getTranslation(section.translationKey),
      labelFr: this.translationService.getTranslation(section.translationKey), // Will be updated when language changes
      tabIndex: section.tabIndex
    }));
  }

  /**
   * Gets all section IDs that exist in the DOM
   */
  getSectionIds(): string[] {
    return this.extractNavigationItems().map(item => item.id);
  }
} 