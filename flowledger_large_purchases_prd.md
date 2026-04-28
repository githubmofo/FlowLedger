# FlowLedger – Large Purchases, EMI & Loan Module PRD

## Document Overview
- **Product:** FlowLedger
- **Platform:** Android (Java)
- **Feature Module:** Large Purchases + EMI & Loan Tracking
- **Purpose:** Separate and enhance tracking of high-value transactions and financed purchases

## Background
FlowLedger currently focuses on fast logging of day-to-day expenses using payment modes like UPI, Cash, and Card. This works well for small and medium transactions, but larger purchases (phone, laptop, furniture, travel, car-related costs, etc.) have a bigger impact on a user’s finances and often involve EMI or loans.

To give users better control and clarity, FlowLedger should introduce a dedicated **Large Purchases** section with additional metadata such as **Purchase Type (One-time / EMI / Loan)** and separated analytics (charts, allocation, search, recents) specific to those big expenses.

## Product Goals
- Separate high-impact purchases from everyday transactions.
- Provide more meaningful insights on big spending and financing commitments.
- Support EMI and loan-based purchases without overcomplicating daily logging.
- Maintain a clean and simple experience by introducing this as a focused section, not another heavy app.

## Problem Statement
In the current app, a ₹60,000 phone purchase looks similar to a ₹60 coffee in the transaction history and insights. Large purchases distort daily and weekly analytics, and EMI/loan-based purchases are not explicitly visible. Users cannot easily answer questions like:
- How much have I spent on big items this year?
- How many active EMIs do I currently have?
- What portion of my monthly outflow is from large purchases vs small daily expenses?

## Solution Overview
Introduce a **Large Purchases** module that:
- Lets users explicitly mark and manage high-value transactions.
- Separates payment method (Cash/UPI/Card/Transfer) from purchase type (One-time/EMI/Loan).
- Provides dedicated charts, allocation blocks, search filters, and recents view for large purchases.
- Keeps the first version simple and focused on visibility, not full loan amortization.

## Key Concepts

### Payment Method (Existing Concept)
How the money was paid:
- UPI
- Cash
- Card
- Bank Transfer (optional later)

### Purchase Type (New Concept)
How the purchase is structured financially:
- **One-time:** Paid fully at purchase.
- **EMI:** Paid over several months with a fixed periodic amount.
- **Loan:** Financed through a personal or auto loan, usually separate from a card or UPI transaction.

Payment Method and Purchase Type are separate fields, so a phone might be:
- Payment Method = Card
- Purchase Type = EMI

or furniture might be:
- Payment Method = UPI
- Purchase Type = One-time

## Scope
### In Scope (V1)
- Large Purchases section
- New transaction type fields for large purchases
- Separate Large Purchases charts and allocation
- Separate Large Purchases search and recents
- Basic EMI/Loan metadata (amount, tenure, EMI value, start date)

### Out of Scope (V1)
- Full loan amortization schedules
- Interest calculations and detailed statements
- Alerts and reminders for EMIs
- Integration with bank accounts

These can be added later as advanced features.

## User Stories

### Large Purchase Creation
- As a user, I want to create a large purchase entry with extra details so I can track important transactions separately from daily expenses.

### EMI & Loan Identification
- As a user, I want to mark a purchase as EMI or Loan so I can see how much of my monthly cash flow comes from financed commitments.

### Dedicated Analytics
- As a user, I want separate charts and allocation for large purchases, so my daily expense charts are not distorted by one or two huge transactions.

### Focused Search & Recents
- As a user, I want a dedicated list and search section for big purchases so I can quickly review major financial decisions.

## Detailed Requirements

## 1. Large Purchases Section

### Objective
Provide a dedicated space to view, add, and analyze major expenses.

### Access
- Entry from home: a card or shortcut saying **"Large Purchases"**.
- Optionally, a tab or sub-section in Insights.

### Data Fields for Each Large Purchase
- **Title:** e.g., "iPhone 15", "Office Chair", "Bike Service", "Fridge".
- **Amount:** total cost.
- **Category:** Electronics, Furniture, Vehicle, Travel, etc.
- **Payment Method:** UPI / Cash / Card / Transfer.
- **Purchase Type:** One-time / EMI / Loan.
- **Purchase Date.**
- **Note (optional).**

Additional fields when Purchase Type = EMI:
- **EMI Amount.**
- **Number of Installments (months).**
- **Start Date.**

Additional fields when Purchase Type = Loan:
- **Financed Amount.**
- **EMI Amount or Monthly Payment.**
- **Tenure (months).**
- **Loan Start Date.**
- (Interest rate field can be optional for future use.)

### UI Behavior
- Large Purchases form should feel like an extended version of Add Expense, but with more structured fields.
- Smart defaults: Amount and category can be carried over from a regular transaction if user “promotes” it to a large purchase.
- Purchase Type selection dynamically shows/hides relevant fields.

## 2. Data Model Changes

### Transactions Table (Existing)
- id
- amount
- category_id
- payment_mode
- note
- timestamp

Add fields or create a parallel table to support:
- is_large_purchase (boolean)
- purchase_type (ONE_TIME / EMI / LOAN / NULL)
- large_purchase_id (foreign key if separate table is used)

### LargePurchases Table (Option A – Separate Table)
| Field              | Type    | Description                              |
|--------------------|---------|------------------------------------------|
| id                 | INTEGER | Unique ID                                |
| title              | TEXT    | Name / label of purchase                 |
| amount             | REAL    | Total cost                               |
| category_id        | INTEGER | Category reference                       |
| payment_method     | TEXT    | UPI / Cash / Card / Transfer            |
| purchase_type      | TEXT    | ONE_TIME / EMI / LOAN                   |
| purchase_date      | INTEGER | Timestamp                                |
| note               | TEXT    | Optional note                            |
| emi_amount         | REAL    | EMI amount (if EMI or Loan)             |
| emi_months         | INTEGER | Number of installments / months         |
| loan_principal     | REAL    | Financed amount (Loan)                  |
| created_at         | INTEGER | Record creation time                     |

Option B is to keep everything in Transactions with flags, but a separate table gives more control and clarity for future features.

## 3. EMI & Loan Metadata (V1 Simplification)

### EMI
- Treat EMI mostly as **context**, not a full repayment engine.
- Show total purchase amount, EMI amount, and total months.
- Optionally, show “Total EMI commitment per month from all EMI purchases.”

### Loan
- Focus on visibility, not exact amortization.
- Show loan-backed large purchases and total EMI for loans.

This gives immediate value without requiring complex calculations.

## 4. Charts for Large Purchases

### Objectives
- Separate large purchase behavior from daily expenses.

### Chart Types
- **Category Breakdown (Large Purchases Only):**
  - Donut or pie showing Electronics vs Vehicle vs Furniture vs Travel etc.
- **Purchase Type Split:**
  - One-time vs EMI vs Loan.
- **Timeline Chart:**
  - Simple bar or point chart of big purchases by month.

### Display Examples
- "Large Purchases This Year" – total amount and count.
- "EMI vs One-time" – share of large purchases.
- "Top 3 Large Purchase Categories".

Charts should be accessible from a **Large Purchases** section in Insights.

## 5. Allocation for Large Purchases

### Objective
Show how major expenses are distributed.

### Types of Allocation
- **By Category:** Electronics, Vehicle, Furniture, Travel, etc.
- **By Purchase Type:** One-time, EMI, Loan.

### UI
- Use progress bars or mini charts in a dedicated "Large Purchase Allocation" block.
- Separate this from standard daily expense allocation.

## 6. Search & Filter for Large Purchases

### Objective
Let users quickly find and review big purchases.

### Core Filters
- By title/keyword.
- By category.
- By purchase type (One-time / EMI / Loan).
- By payment method.
- By date range.
- By amount range (e.g., > ₹10,000).

### UX
- Provide a dedicated **Large Purchases** search view, or
- Add filters in the main search to show "Only Large Purchases".

## 7. Recents – Large Purchases

### Objective
Highlight recent major purchases separately from normal transaction list.

### Requirements
- A mini section on Home or in Insights that lists the last 3–5 large purchases.
- Each row shows:
  - Title (e.g., "Laptop"),
  - Amount,
  - Purchase type icon (One-time / EMI / Loan),
  - Category label, and date.

### UX Considerations
- Use different visual treatment from daily transactions (larger, more prominent cards).
- Avoid overwhelming the Home screen; make it a separate card/section.

## 8. Interaction with Existing Features

### Daily Expenses
- Daily expenses continue to use the existing transaction flow.
- Big purchases can either be created directly in Large Purchases or promoted from a regular transaction.

### Insights (Standard)
- Standard charts should either exclude large purchases by default or clearly indicate their impact.
- Optionally, provide a toggle: "Include Large Purchases in Daily Insights".

### Allocation (Standard)
- Remains focused on all expenses or daily-mid sized categories.
- Large purchase allocation is shown in its own section to keep clarity.

## 9. UX & Design Notes

### Simplicity First
- The UI for creating large purchases must remain simple.
- Use progressive disclosure: only show EMI/Loan fields when that type is selected.

### Clarity of Concepts
- Payment Method should always be UPI/Cash/Card/Transfer.
- Purchase Type should always be One-time/EMI/Loan.
- Do not mix them into a single dropdown.

### Visual Differentiation
- Use a distinct but related visual style for Large Purchases cards.
- Consider using icons/badges for EMI and Loan.

## 10. Technical Notes

### Implementation Strategy
- Introduce the LargePurchases table (or flags in Transactions) via a schema migration.
- Create CRUD operations for large purchases.
- Update Insights logic to read from both Transactions and LargePurchases.
- Implement filters in search and recents.

### Performance
- Expect fewer entries than daily expenses, so performance is not a major concern.
- Ensure indexes on purchase_date and purchase_type for fast filtering.

## 11. Acceptance Criteria

### Data & Creation
- User can create a large purchase with payment method and purchase type.
- EMI and Loan fields appear only when relevant.

### Analytics
- Large Purchases charts show data once at least one large purchase exists.
- Allocation reflects correct category and type splits.

### Search & Recents
- Large Purchases can be filtered independently.
- Recent large purchases show correctly ordered by date.

### UI/UX
- Daily expense screens remain simple.
- Users can clearly distinguish between daily expenses and large purchases.
- EMI/Loan concepts are presented without overwhelming the user.

## 12. Future Enhancements (Beyond V1)
- EMI reminders and push notifications.
- Full loan amortization calculation and remaining balance view.
- Goal tracking ("Save for Laptop", then convert goal → Large Purchase).
- Integration with bank SMS parsing to auto-detect big purchases.

FlowLedger’s Large Purchases module should strengthen the app’s position as a smart, habit-friendly finance tracker that respects the difference between everyday expenses and major financial decisions.
