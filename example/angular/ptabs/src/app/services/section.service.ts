import { Injectable } from '@angular/core';
import { NavigationItem } from '../navigation/navigation.component';

@Injectable({
  providedIn: 'root'
})
export class SectionService {
  
  /**
   * Extracts navigation items from sections with IDs in the DOM
   */
  extractNavigationItems(): NavigationItem[] {
    const sections = [
      // Top sections (outside tabs)
      { id: 'key-features', label: 'Key Features' },
      { id: 'technical-implementation', label: 'Technical Implementation' },
      { id: 'notes', label: 'Notes' },
      
      // Details tab sections only
      { id: 'project-overview', label: 'Project Overview', tabIndex: 0 },
      { id: 'technical-architecture', label: 'Technical Architecture', tabIndex: 0 },
      { id: 'user-interface-design', label: 'User Interface Design', tabIndex: 0 },
      { id: 'component-integration', label: 'Component Integration', tabIndex: 0 },
      { id: 'future-enhancements', label: 'Future Enhancements', tabIndex: 0 }
      
      // History tab sections removed - not shown in navigation
    ];

    // Filter to only include sections that actually exist in the DOM
    return sections.filter(section => {
      const element = document.getElementById(section.id);
      return element !== null;
    });
  }

  /**
   * Gets all section IDs that exist in the DOM
   */
  getSectionIds(): string[] {
    return this.extractNavigationItems().map(item => item.id);
  }
} 