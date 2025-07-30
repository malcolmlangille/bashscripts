import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of, BehaviorSubject } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';

export interface TranslationData {
  navigation: {
    title: string;
    keyFeatures: string;
    technicalImplementation: string;
    notes: string;
    projectOverview: string;
    technicalArchitecture: string;
    userInterfaceDesign: string;
    componentIntegration: string;
    futureEnhancements: string;
  };
  tabs: {
    details: string;
    history: string;
  };
  common: {
    languageToggle: string;
  };
}

@Injectable({
  providedIn: 'root'
})
export class TranslationService {
  private currentLanguage = 'en';
  private translations: { [key: string]: TranslationData } = {};
  private languageChangeSubject = new BehaviorSubject<string>('en');

  constructor(private http: HttpClient) {
    // Initialize with default translations
    this.translations['en'] = this.getDefaultTranslations();
    this.translations['fr'] = this.getDefaultTranslations();
  }

  setLanguage(language: 'en' | 'fr'): void {
    console.log('Setting language to:', language);
    this.currentLanguage = language;
    this.languageChangeSubject.next(language);
  }

  getCurrentLanguage(): string {
    return this.currentLanguage;
  }

  getLanguageChangeObservable(): Observable<string> {
    return this.languageChangeSubject.asObservable();
  }

  loadTranslations(language: 'en' | 'fr'): Observable<TranslationData> {
    console.log(`Loading translations for language: ${language}`);
    
    return this.http.get<TranslationData>(`/assets/i18n/${language}.json`)
      .pipe(
        tap(data => {
          console.log(`Loaded ${language} translations:`, data);
          this.translations[language] = data;
        }),
        catchError(error => {
          console.error(`Error loading ${language} translations:`, error);
          console.log('Using default translations for:', language);
          return of(this.getDefaultTranslations());
        })
      );
  }

  getTranslation(key: string): string {
    const keys = key.split('.');
    let current: any = this.translations[this.currentLanguage];
    
    for (const k of keys) {
      if (current && current[k] !== undefined) {
        current = current[k];
      } else {
        console.warn(`Translation key not found: ${key} for language: ${this.currentLanguage}`);
        return key; // Return key if translation not found
      }
    }
    
    const result = typeof current === 'string' ? current : key;
    console.log(`Translation for ${key}: ${result}`);
    return result;
  }

  private getDefaultTranslations(): TranslationData {
    return {
      navigation: {
        title: 'Navigation',
        keyFeatures: 'Key Features',
        technicalImplementation: 'Technical Implementation',
        notes: 'Notes',
        projectOverview: 'Project Overview',
        technicalArchitecture: 'Technical Architecture',
        userInterfaceDesign: 'User Interface Design',
        componentIntegration: 'Component Integration',
        futureEnhancements: 'Future Enhancements'
      },
      tabs: {
        details: 'Details',
        history: 'History'
      },
      common: {
        languageToggle: 'EN'
      }
    };
  }
} 