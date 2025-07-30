import { Component, signal, ViewChild, AfterViewInit, ChangeDetectorRef, HostListener } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { NavigationComponent, NavigationItem } from './navigation/navigation.component';
import { SectionService } from './services/section.service';
import { DetailsComponent } from './details/details.component';
import { TabsModule } from 'primeng/tabs';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, NavigationComponent, DetailsComponent, TabsModule],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements AfterViewInit {
  protected readonly title = signal('ptabs');
  protected readonly activeTabIndex = signal(0);
  protected readonly activeSection = signal('');
  private isManualNavigation = false;
  private isProgrammaticScroll = false;
  private manualNavigationTimeout: any = null;
  private lastManualNavigationTime: number | null = null;

  // Navigation items - will be populated dynamically
  protected navigationItems: NavigationItem[] = [];

  @ViewChild('tabs') tabs: any;

  constructor(
    private cdr: ChangeDetectorRef,
    private sectionService: SectionService
  ) {}

  ngAfterViewInit() {
    // Initialize navigation items after view is ready
    this.initializeNavigationItems();
    this.updateActiveSection();
  }

  private initializeNavigationItems(): void {
    // Get navigation items from the service
    this.navigationItems = this.sectionService.extractNavigationItems();
    console.log('Navigation items initialized:', this.navigationItems);
  }

  @HostListener('window:scroll')
  onScroll() {
    this.updateActiveSection();
  }

  private updateActiveSection(): void {
    if (this.isManualNavigation || this.isProgrammaticScroll) {
      console.log('Skipping scroll tracking - manual navigation or programmatic scroll in progress');
      const now = Date.now();
      if (this.lastManualNavigationTime && (now - this.lastManualNavigationTime) > 2000) {
        console.log('Safety clearing flags - they have been set for too long');
        this.isManualNavigation = false;
        this.isProgrammaticScroll = false;
        this.manualNavigationTimeout = null;
      }
      return;
    }

    const sections = this.sectionService.getSectionIds();
    const scrollPosition = window.scrollY + 50; // Reduced offset for more sensitive detection
    let foundActiveSection = false;

    console.log('=== updateActiveSection called ===');
    console.log('Scroll position:', scrollPosition);
    console.log('Window scrollY:', window.scrollY);
    console.log('Document height:', document.documentElement.scrollHeight);
    console.log('Window height:', window.innerHeight);
    console.log('Current active section:', this.activeSection());

    for (const sectionId of sections) {
      const element = document.getElementById(sectionId);
      if (element) {
        const rect = element.getBoundingClientRect();

        console.log(`Section ${sectionId}: height=${rect.height}, top=${rect.top}, bottom=${rect.bottom}`);

        // Only consider elements that are actually visible (height > 0)
        if (rect.height > 0) {
          const elementTop = rect.top + window.scrollY;
          const elementBottom = elementTop + rect.height;

          console.log(`Section ${sectionId}: elementTop=${elementTop}, elementBottom=${elementBottom}`);

          // Check if the scroll position is within this section
          if (scrollPosition >= elementTop && scrollPosition < elementBottom) {
            console.log('Setting active section to:', sectionId, 'at scroll position:', scrollPosition);
            this.activeSection.set(sectionId);
            foundActiveSection = true;
            break;
          }
        } else {
          console.log(`Section ${sectionId}: hidden (height=0)`);
        }
      } else {
        console.log(`Section ${sectionId}: element not found`);
      }
    }

    // If no section found, try to find the closest one
    if (!foundActiveSection) {
      let closestSection = '';
      let closestDistance = Infinity;

      for (const sectionId of sections) {
        const element = document.getElementById(sectionId);
        if (element && element.getBoundingClientRect().height > 0) {
          const rect = element.getBoundingClientRect();
          const elementTop = rect.top + window.scrollY;
          const distance = Math.abs(scrollPosition - elementTop);

          console.log(`Closest check - ${sectionId}: distance=${distance}`);

          if (distance < closestDistance) {
            closestDistance = distance;
            closestSection = sectionId;
          }
        }
      }

      if (closestSection) {
        console.log('Setting closest section to:', closestSection, 'distance:', closestDistance);
        this.activeSection.set(closestSection);
      }
    }
  }

  public scrollToSection(sectionId: string, tabIndex?: number): void {
    console.log('=== scrollToSection called ===');
    console.log('sectionId:', sectionId);
    console.log('tabIndex:', tabIndex);

    // Clear any existing timeout
    if (this.manualNavigationTimeout) {
      clearTimeout(this.manualNavigationTimeout);
    }

    // Set the active section immediately when clicking navigation
    this.activeSection.set(sectionId);
    this.isManualNavigation = true;
    this.isProgrammaticScroll = true;
    this.lastManualNavigationTime = Date.now(); // Record the time when navigation starts

    // Clear the manual navigation flag after a shorter delay
    this.manualNavigationTimeout = setTimeout(() => {
      this.isManualNavigation = false;
      this.isProgrammaticScroll = false;
      this.manualNavigationTimeout = null;
      console.log('Manual navigation flags cleared');
    }, 500); // Reduced to 500ms

    // If a tab index is provided, switch to that tab first
    if (tabIndex !== undefined) {
      console.log('Switching to tab:', tabIndex);

      // Update the active tab index
      this.activeTabIndex.set(tabIndex);

      // Force change detection
      this.cdr.detectChanges();

      // Wait longer for the tab to be visible, then scroll
      setTimeout(() => {
        console.log('About to scroll to element after tab switch...');
        console.log('Current active tab index:', this.activeTabIndex());
        this.scrollToElement(sectionId);
      }, 200); // Increased delay to ensure tab switching completes
    } else {
      console.log('No tab index provided, scrolling directly...');
      this.scrollToElement(sectionId);
    }
  }

  private scrollToElement(sectionId: string): void {
    console.log('=== scrollToElement called ===');
    console.log('Looking for sectionId:', sectionId);

    // Try to find the element
    let element = document.getElementById(sectionId);
    console.log('Element found globally:', !!element);

    if (!element) {
      console.log('Element not found globally, checking tab panels...');
      // If not found, try to find it within any tab panel
      // Try multiple possible PrimeNG v20 selectors
      const tabPanels = document.querySelectorAll('.p-tabpanel, .p-tabs-panel, [data-pc-name="tabpanel"]');
      console.log('Found tab panels:', tabPanels.length);

      for (const panel of tabPanels) {
        console.log('Checking panel:', panel);
        element = panel.querySelector(`#${sectionId}`);
        if (element) {
          console.log('Element found in tab panel:', sectionId);
          break;
        }
      }
    }

    if (element) {
      console.log('Element found, scrolling to:', sectionId);
      console.log('Element rect:', element.getBoundingClientRect());

      element.scrollIntoView({
        behavior: 'smooth',
        block: 'center'
      });
    } else {
      console.log('Element not found:', sectionId);
      // Try to find the element in the entire document
      const allElements = document.querySelectorAll('[id]');
      console.log('Available elements with IDs:', Array.from(allElements).map(el => el.id));

      // Also log the active tab panel content - try multiple selectors
      const activeTabPanel = document.querySelector('.p-tabpanel-active, .p-tabs-panel-active, [data-pc-name="tabpanel"][aria-hidden="false"]');
      if (activeTabPanel) {
        console.log('Active tab panel content:', activeTabPanel.innerHTML);
      }
    }
  }

  // Handle navigation component events
  onNavigationClick(event: {sectionId: string, tabIndex?: number}): void {
    this.scrollToSection(event.sectionId, event.tabIndex);
  }
}
